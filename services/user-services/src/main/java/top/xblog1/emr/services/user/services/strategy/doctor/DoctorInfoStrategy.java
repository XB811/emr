package top.xblog1.emr.services.user.services.strategy.doctor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import top.xblog1.emr.framework.starter.cache.DistributedCache;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.services.user.common.constant.UserExecuteStrategyContant;
import top.xblog1.emr.services.user.dao.entity.AdminDO;
import top.xblog1.emr.services.user.dao.entity.DoctorDO;
import top.xblog1.emr.services.user.dao.mapper.DoctorMapper;
import top.xblog1.emr.services.user.dto.req.UserUpdateReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.strategy.AbstractUserExecuteStrategy;
import top.xblog1.emr.services.user.toolkit.PasswordEncryptUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.USER_REGISTER_PHONE_ADMIN;
import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.USER_REGISTER_PHONE_DOCTOR;
import static top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum.HAS_PHONE;

/**
 * 医生信息策略
 */
@Component
@RequiredArgsConstructor
public class DoctorInfoStrategy extends AbstractUserExecuteStrategy {
    private final DoctorMapper doctorMapper;
    private final DistributedCache distributedCache;
    public String mark(){
        return UserTypeEnum.DOCTOR.code()+ UserExecuteStrategyContant.USER_INFO_STRATEGY_SUFFIX;
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
    /*
    更新用户信息
     */
    public void update(BaseUserDTO baseUserDTO) {
        //拆包
        UserUpdateReqDTO requestParam = baseUserDTO.getUserUpdateReqDTO();
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        //查询用户
        baseUserDTO.setId(Long.valueOf(requestParam.getId()));
        String phone = queryActualUserByID(baseUserDTO)
                .getUserQueryActualRespDTO()
                .getPhone();
        //更新redis中的手机号
        if(!Objects.equals(phone, requestParam.getPhone())){
            instance.opsForSet().remove(USER_REGISTER_PHONE_DOCTOR, phone);
            Long add = instance.opsForSet().add(USER_REGISTER_PHONE_DOCTOR, requestParam.getPhone());
            if(add==null||add==0){
                throw new ClientException(HAS_PHONE);
            }
        }
        DoctorDO doctorDO = BeanUtil.convert(requestParam, DoctorDO.class);
        //密码加密
        doctorDO.setPassword(PasswordEncryptUtil.encryptPassword(requestParam.getPassword()));
        LambdaUpdateWrapper<DoctorDO> adminUpdateWrapper = Wrappers.lambdaUpdate(DoctorDO.class)
                .eq(DoctorDO::getId, doctorDO.getId());
        doctorMapper.update(doctorDO, adminUpdateWrapper);
    }
    /*
    根据id查询用户未脱敏信息
     */
    public BaseUserDTO queryActualUserByID(BaseUserDTO baseUserDTO) {
        //拆包
        Long userId = baseUserDTO.getId();
        LambdaQueryWrapper<DoctorDO> queryWrapper = Wrappers.lambdaQuery(DoctorDO.class)
                .eq(DoctorDO::getId, userId);
        DoctorDO doctorDO = doctorMapper.selectOne(queryWrapper);
        if (doctorDO == null) {
            throw new ClientException("用户ID不存在或已注销，请检查用户名是否正确");
        }
        //封包返回
        return BaseUserDTO.builder()
                .userQueryActualRespDTO(BeanUtil.convert(doctorDO, UserQueryActualRespDTO.class))
                .build();
    }
}
