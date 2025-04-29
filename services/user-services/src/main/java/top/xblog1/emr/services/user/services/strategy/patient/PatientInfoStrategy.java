package top.xblog1.emr.services.user.services.strategy.patient;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.xblog1.emr.framework.starter.cache.DistributedCache;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.services.user.common.constant.UserExecuteStrategyContant;
import top.xblog1.emr.services.user.dao.entity.AdminDO;
import top.xblog1.emr.services.user.dao.entity.PatientDO;
import top.xblog1.emr.services.user.dao.entity.PatientPhoneReuseDO;
import top.xblog1.emr.services.user.dao.mapper.PatientMapper;
import top.xblog1.emr.services.user.dao.mapper.PatientPhoneReuseMapper;
import top.xblog1.emr.services.user.dto.req.UserUpdateReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.strategy.AbstractUserExecuteStrategy;
import top.xblog1.emr.services.user.toolkit.PasswordEncryptUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.PATIENT_REGISTER_PHONE_REUSE_SHARDING;
import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.USER_REGISTER_PHONE_ADMIN;
import static top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum.HAS_PHONE;
import static top.xblog1.emr.services.user.toolkit.UserReuseUtil.hashShardingIdx;

/**
 * 患者信息管理策略
 */
@Component
@RequiredArgsConstructor
public class PatientInfoStrategy extends AbstractUserExecuteStrategy {

    private final PatientMapper patientMapper;
    private final DistributedCache distributedCache;
    private final RBloomFilter<String> patientRegisterPhoneCachePenetrationBloomFilter;
    private final PatientPhoneReuseMapper patientPhoneReuseMapper;

    public String mark(){
        return UserTypeEnum.PATIENT.code()+ UserExecuteStrategyContant.USER_INFO_STRATEGY_SUFFIX;
    }
    @PostConstruct
    protected void init() {
        // 获取当前类的所有方法
        Method[] methods = this.getClass().getDeclaredMethods();

        // 过滤并缓存方法
        Arrays.stream(methods)
                .filter(method -> method.getParameterCount() == 1 && method.getParameterTypes()[0] == BaseUserDTO.class)
                .forEach(method -> methodCache.put(method.getName(), method));
    }
    @Transactional(rollbackFor = Exception.class)
    public void update(BaseUserDTO baseUserDTO) {
        //拆包
        UserUpdateReqDTO requestParam = baseUserDTO.getUserUpdateReqDTO();
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        //查询用户
        if(requestParam.getPhone()!=null&&!requestParam.getPhone().isEmpty()) {
            baseUserDTO.setId(Long.valueOf(requestParam.getId()));
            String oldPhone = queryActualUserByID(baseUserDTO)
                    .getUserQueryActualRespDTO()
                    .getPhone();
            //更新布隆过滤器和复用表中的手机号
            if (requestParam.getPhone()!=null&&!Objects.equals(oldPhone, requestParam.getPhone())) {
                String newPhone = requestParam.getPhone();
                //查询新号码是否可用
                if (patientRegisterPhoneCachePenetrationBloomFilter.contains(newPhone)) {
                    throw new ClientException(HAS_PHONE);
                }
                //旧号码加入复用表
                patientPhoneReuseMapper.insert(new PatientPhoneReuseDO(oldPhone));
                instance.opsForSet().add(PATIENT_REGISTER_PHONE_REUSE_SHARDING + hashShardingIdx(oldPhone), oldPhone);
                //新号码加入布隆过滤器，从复用表删除
                patientPhoneReuseMapper.delete(Wrappers.update(new PatientPhoneReuseDO(newPhone)));
                instance.opsForSet().remove(PATIENT_REGISTER_PHONE_REUSE_SHARDING + hashShardingIdx(newPhone), newPhone);
                patientRegisterPhoneCachePenetrationBloomFilter.add(newPhone);

            }
        }
        PatientDO patientDO = BeanUtil.convert(requestParam, PatientDO.class);
        if(requestParam.getPassword()!=null&&!requestParam.getPassword().isEmpty()) {
            //密码加密
            patientDO.setPassword(PasswordEncryptUtil.encryptPassword(requestParam.getPassword()));
        }
        LambdaUpdateWrapper<PatientDO> patientUpdateWrapper = Wrappers.lambdaUpdate(PatientDO.class)
                .eq(PatientDO::getId, patientDO.getId());
        patientMapper.update(patientDO, patientUpdateWrapper);
    }

    public BaseUserDTO queryActualUserByID(BaseUserDTO baseUserDTO) {
        //拆包
        Long userId = baseUserDTO.getId();
        LambdaQueryWrapper<PatientDO> queryWrapper = Wrappers.lambdaQuery(PatientDO.class)
                .eq(PatientDO::getId, userId);
        PatientDO patientDO = patientMapper.selectOne(queryWrapper);
        if (patientDO == null) {
            throw new ClientException("用户ID不存在或已注销，请检查用户名是否正确");
        }
        //封包返回
        return BaseUserDTO.builder()
                .userQueryActualRespDTO(BeanUtil.convert(patientDO, UserQueryActualRespDTO.class))
                .build();
    }
}
