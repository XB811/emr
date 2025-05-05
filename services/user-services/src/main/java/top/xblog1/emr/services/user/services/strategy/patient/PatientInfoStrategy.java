package top.xblog1.emr.services.user.services.strategy.patient;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.database.toolkit.PageUtil;
import top.xblog1.emr.framework.starter.user.core.UserContext;
import top.xblog1.emr.services.user.common.constant.UserExecuteStrategyContant;
import top.xblog1.emr.services.user.dao.entity.AdminDO;
import top.xblog1.emr.services.user.dao.entity.PatientDO;
import top.xblog1.emr.services.user.dao.entity.PatientPhoneReuseDO;
import top.xblog1.emr.services.user.dao.mapper.PatientMapper;
import top.xblog1.emr.services.user.dao.mapper.PatientPhoneReuseMapper;
import top.xblog1.emr.services.user.dto.req.UpdatePasswordReqDTO;
import top.xblog1.emr.services.user.dto.req.UserPageQueryReqDTO;
import top.xblog1.emr.services.user.dto.req.UserResetPasswordReqDTO;
import top.xblog1.emr.services.user.dto.req.UserUpdateReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryRespDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.strategy.AbstractUserExecuteStrategy;
import top.xblog1.emr.services.user.toolkit.PasswordEncryptUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.PATIENT_REGISTER_PHONE_REUSE_SHARDING;
import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.USER_LOGIN_PHONE_VERIFY_CODE_PREFIX;
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
                    // 查询复用表中有没有新手机号
                    Boolean member = instance.opsForSet()
                            .isMember(PATIENT_REGISTER_PHONE_REUSE_SHARDING
                                    + hashShardingIdx(newPhone), newPhone);
                    //如果布隆过滤器中有新手机号，且服用表中没有改=新手机号
                    if(Boolean.FALSE.equals(member))
                        throw new ClientException(HAS_PHONE);
                }
                //旧号码加入复用表
                patientPhoneReuseMapper.insert(PatientPhoneReuseDO.builder()
                                            .phone(oldPhone)
                                            .build());
                instance.opsForSet().add(PATIENT_REGISTER_PHONE_REUSE_SHARDING + hashShardingIdx(oldPhone), oldPhone);
                //新号码加入布隆过滤器，从复用表删除
                patientPhoneReuseMapper.delete(Wrappers.update(PatientPhoneReuseDO.builder()
                                                            .phone(newPhone)
                                                            .build()));
                instance.opsForSet().remove(PATIENT_REGISTER_PHONE_REUSE_SHARDING + hashShardingIdx(newPhone), newPhone);
                patientRegisterPhoneCachePenetrationBloomFilter.add(newPhone);

            }
        }
        PatientDO patientDO = BeanUtil.convert(requestParam, PatientDO.class);
        //不需要密码就可以更新密码 只给admin该权限
        // 判断当前登录用户是不是admin
        if(UserContext.getUserType().equals(UserTypeEnum.ADMIN.code()))
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
    public void updatePassword(BaseUserDTO request){
        UpdatePasswordReqDTO requestParam = request.getUpdatePasswordReqDTO();
        if(requestParam.getOldPassword() ==null || requestParam.getOldPassword().isEmpty()){
            throw new ClientException("旧密码不能为空");
        }else if(requestParam.getNewPassword() ==null || requestParam.getNewPassword().isEmpty()){
            throw new ClientException("新密码不能为空");
        }else if(requestParam.getConfirmPassword() ==null || requestParam.getConfirmPassword().isEmpty()) {
            throw new ClientException("重复密码不能为空");
        }else if(requestParam.getNewPassword().length()<6|| requestParam.getNewPassword().length()>16){
            throw new ClientException("新密码的长度为6-16位之间");
        }else if(!requestParam.getNewPassword().equals(requestParam.getConfirmPassword())){
            throw new ClientException("重复密码和新密码输入不一致");
        }
        LambdaQueryWrapper<PatientDO> queryWrapper = Wrappers.lambdaQuery(PatientDO.class)
                .eq(PatientDO::getId, UserContext.getUserId());
        PatientDO patientDO = null;
        try {
            patientDO = patientMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            throw new ClientException("当前登录用户信息丢失");
        }
        if(patientDO==null){
            throw new ClientException("当前登录用户信息丢失");
        }
        if(!PasswordEncryptUtil.verifyPassword(requestParam.getOldPassword(),patientDO.getPassword())){
            throw new ClientException("旧密码错误");
        }
        patientDO.setPassword(PasswordEncryptUtil.encryptPassword(requestParam.getNewPassword()));
        patientMapper.updateById(patientDO);
    }

    public BaseUserDTO pageQuery(BaseUserDTO request){
        //拆包
        UserPageQueryReqDTO requestParam = request.getUserPageQueryReqDTO();
        //分页查询
        LambdaQueryWrapper<PatientDO> queryWrapper = Wrappers.lambdaQuery(PatientDO.class);
        if(requestParam.getUsername()!=null&&!requestParam.getUsername().isEmpty())
                queryWrapper.like(PatientDO::getUsername,requestParam.getUsername());
        if(requestParam.getRealName()!=null&&!requestParam.getRealName().isEmpty())
                queryWrapper.like(PatientDO::getRealName,requestParam.getRealName());
        if(requestParam.getPhone()!=null&&!requestParam.getPhone().isEmpty())
                queryWrapper.like(PatientDO::getPhone,requestParam.getPhone());
        if(requestParam.getIdCard()!=null&&!requestParam.getIdCard().isEmpty())
                queryWrapper.like(PatientDO::getIdCard,requestParam.getIdCard());
        queryWrapper.orderByDesc(PatientDO::getUpdateTime);
        IPage<PatientDO> patientDOIPage =patientMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        PageResponse<UserQueryRespDTO> response = PageUtil.convert(patientDOIPage, each -> {

            return BeanUtil.convert(each, UserQueryRespDTO.class);
        });
        return BaseUserDTO.builder()
                .userPageQueryRespDTO(response)
                .build();

    }
    public void resetPassword(BaseUserDTO request){
        UserResetPasswordReqDTO requestParam = request.getUserResetPasswordReqDTO();
        //先查数据库拿到用户信息
        LambdaQueryWrapper<PatientDO> queryWrapper = Wrappers.lambdaQuery(PatientDO.class)
                .eq(PatientDO::getPhone,requestParam.getPhone());
        PatientDO patientDO = patientMapper.selectOne(queryWrapper);
        if(patientDO==null){
            throw new ClientException("该用户不存在");
        }
        //再查缓存拿到验证码
        String cacheCode = distributedCache.get(USER_LOGIN_PHONE_VERIFY_CODE_PREFIX + UserTypeEnum.PATIENT.code(), String.class);
        //如果验证码不同
        if(!cacheCode.equals(requestParam.getCode()))
            throw new ClientException("验证码错误");
        //如果相同，更新密码
        patientDO.setPassword(PasswordEncryptUtil.encryptPassword(requestParam.getPassword()));
        patientMapper.updateById(patientDO);
    }
}
