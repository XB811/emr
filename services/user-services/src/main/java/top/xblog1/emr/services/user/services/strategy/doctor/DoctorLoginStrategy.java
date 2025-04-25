package top.xblog1.emr.services.user.services.strategy.doctor;

import cn.hutool.system.UserInfo;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.xblog1.emr.framework.starter.cache.DistributedCache;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.AbstractException;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.framework.starter.designpattern.chain.AbstractChainContext;
import top.xblog1.emr.framework.starter.user.core.UserInfoDTO;
import top.xblog1.emr.framework.starter.user.toolkit.JWTUtil;
import top.xblog1.emr.services.user.common.constant.UserExecuteStrategyContant;
import top.xblog1.emr.services.user.common.enums.UserChainMarkEnum;
import top.xblog1.emr.services.user.dao.entity.DoctorDO;
import top.xblog1.emr.services.user.dao.mapper.DoctorMapper;
import top.xblog1.emr.services.user.dto.req.UserDeletionReqDTO;
import top.xblog1.emr.services.user.dto.req.UserLoginReqDTO;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserLoginRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserRegisterRespDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.strategy.AbstractUserExecuteStrategy;
import top.xblog1.emr.services.user.toolkit.PasswordEncryptUtil;

import javax.swing.text.html.Option;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.*;
import static top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum.*;
import static top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum.HAS_USERNAME_NOTNULL;

/**
 * 医生登录策略
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DoctorLoginStrategy extends AbstractUserExecuteStrategy {
    private final AbstractChainContext<UserRegisterReqDTO> abstractChainContext;
    private final RedissonClient redissonClient;
    private final DoctorMapper doctorMapper;
    private final DistributedCache distributedCache;

    public String mark(){
        return UserTypeEnum.DOCTOR.code()+ UserExecuteStrategyContant.USER_LOGIN_STRATEGY_SUFFIX;
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
    用户注册
     */
    public BaseUserDTO register(BaseUserDTO baseUserDTO) {
        //拆包
        UserRegisterReqDTO requestParam = baseUserDTO.getUserRegisterReqDTO();
        //从redis获取用户名
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        Integer username_suffix = Integer.valueOf(Objects.requireNonNull(instance.opsForValue().get(USER_REGISTER_USERNAME_DOCTOR)));
        requestParam.setUsername(UserTypeEnum.DOCTOR.code()+String.format("%08d", username_suffix));
        //username_suffix自增后存会redis
        instance.opsForValue().set(USER_REGISTER_USERNAME_DOCTOR, String.valueOf(username_suffix+1));
        //数据校验
        abstractChainContext.handler(UserChainMarkEnum.USER_REGISTER_FILTER.name(), requestParam);
        //密码加密
        requestParam.setPassword(PasswordEncryptUtil.encryptPassword(requestParam.getPassword()));
        //获取用户名注册锁
        RLock lock = redissonClient.getLock(LOCK_DOCTOR_REGISTER + requestParam.getUsername());
        //写入数据库
        boolean tryLock = lock.tryLock();
        if (!tryLock) {
            throw new ServiceException(HAS_USERNAME_NOTNULL);
        }
        try{
            //尝试把手机号写入redis
            if(Boolean.TRUE.equals(instance.opsForSet().isMember(USER_REGISTER_PHONE_DOCTOR, requestParam.getPhone())))
                throw new ClientException(HAS_PHONE);
            instance.opsForSet().add(USER_REGISTER_PHONE_DOCTOR, requestParam.getPhone());
            //尝试写入数据库
            try {
                int inserted = doctorMapper.insert(BeanUtil.convert(requestParam, DoctorDO.class));
                if (inserted < 1) {
                    throw new ServiceException(USER_REGISTER_FAIL);
                }
            } catch (DuplicateKeyException dke) {
                log.error("用户{} 重复注册", requestParam);
                throw new ServiceException(HAS_USERNAME_NOTNULL);
            }
        }catch (AbstractException ex){
            throw ex;
        } finally {
            lock.unlock();
        }
        UserRegisterRespDTO response = BeanUtil.convert(requestParam, UserRegisterRespDTO.class);
        //封包
        return BaseUserDTO.builder()
                .userRegisterRespDTO(response)
                .build();
    }
    /**
     * 根据用户名 用户注销
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletion(BaseUserDTO baseUserDTO){
        //拆包
        UserDeletionReqDTO requestParam = baseUserDTO.getUserDeletionReqDTO();
        RLock lock = redissonClient.getLock(USER_DELETION + requestParam.getUsername());
        lock.lock();
        try {
            //查询到用户并删除
            LambdaQueryWrapper<DoctorDO> queryWrapper = Wrappers.lambdaQuery(DoctorDO.class)
                    .eq(DoctorDO::getUsername, requestParam.getUsername());
            DoctorDO doctorDO = doctorMapper.selectOne(queryWrapper);
            //更新 删除时间
            doctorDO.setUpdateTime(null);
            doctorMapper.updateById(doctorDO);
            doctorMapper.deleteById(doctorDO.getId());
            //删除redis中已注册手机号
            StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
            instance.opsForSet().remove(USER_REGISTER_PHONE_DOCTOR,doctorDO.getPhone());
            //删除redis中的登录信息
            String token = distributedCache.get(USER_LOGIN_DOCTOR_TOKEN_PREFIX+ doctorDO.getId(),String.class);
            if (!Objects.isNull(token)) {
                distributedCache.delete(token);
                distributedCache.delete(USER_LOGIN_DOCTOR_TOKEN_PREFIX + doctorDO.getId());
            }

        }finally {
            lock.unlock();
        }
    }
    /**
     * 用户名是否存在
     */
    public BaseUserDTO hasName(BaseUserDTO baseUserDTO) {
        String username = baseUserDTO.getUsername();
        //获取用户名后缀
        Integer usernameSuffix= Integer.valueOf(username.replace(UserTypeEnum.DOCTOR.code(),""));
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        //获取redis中最新的用户名
        Integer redisUsernameSuffix = Integer.valueOf(instance.opsForValue().get(USER_REGISTER_USERNAME_DOCTOR));
        //如果查询的用户名小于redis中缓存的用户名，则存在，返回true
        return BaseUserDTO.builder()
                .hasUsername(usernameSuffix<redisUsernameSuffix)
                .build();
    }
    /**
     * 用户登录
     */
    public BaseUserDTO login(BaseUserDTO baseUserDTO){
        //拆包
        UserLoginReqDTO requestParam = baseUserDTO.getUserLoginReqDTO();
        //根据username或者手机号分别查询
        LambdaQueryWrapper<DoctorDO> queryWrapperByUsername = Wrappers.lambdaQuery(DoctorDO.class)
                .eq(DoctorDO::getUsername, requestParam.getUsernameOrPhone());
        LambdaQueryWrapper<DoctorDO> queryWrapperByPhone = Wrappers.lambdaQuery(DoctorDO.class)
                .eq(DoctorDO::getPhone, requestParam.getUsernameOrPhone());
        //查询
        DoctorDO doctorDO = Optional.ofNullable(doctorMapper.selectOne(queryWrapperByUsername))
                .or(() -> Optional.ofNullable(doctorMapper.selectOne(queryWrapperByPhone)))
                .orElseThrow(()->new ClientException("用户名/手机号不存在"));
        //判断密码是否正确
        if(Objects.isNull(requestParam.getPassword())|| requestParam.getPassword().isEmpty()){
            throw new ClientException(PASSWORD_NOTNULL);
        }
        if(!PasswordEncryptUtil.verifyPassword(requestParam.getPassword(),doctorDO.getPassword())){
            throw new ClientException("密码错误");
        }
        //构建返回值
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userId(String.valueOf(doctorDO.getId()))
                .username(doctorDO.getUsername())
                .realName(doctorDO.getRealName())
                .userType(UserTypeEnum.DOCTOR.code())
                .build();
        String accessToken = JWTUtil.generateAccessToken(userInfoDTO);
        UserLoginRespDTO actual = UserLoginRespDTO.builder()
                .userId(userInfoDTO.getUserId())
                .username(userInfoDTO.getUsername())
                .realName(userInfoDTO.getRealName())
                .userType(userInfoDTO.getUserType())
                .accessToken(accessToken)
                .build();
        //删除redis中可能缓存的token
        String tokenName =USER_LOGIN_DOCTOR_TOKEN_PREFIX+doctorDO.getId();
        if(distributedCache.get(tokenName,String.class)!=null){
            distributedCache.delete(distributedCache.get(tokenName,String.class));
            distributedCache.delete(tokenName);
        }
        //存入redis
        distributedCache.put(accessToken, JSON.toJSONString(actual), 30, TimeUnit.MINUTES);
        distributedCache.put(tokenName,accessToken,30, TimeUnit.MINUTES);
        //包装返回
        return BaseUserDTO.builder()
                .userLoginRespDTO(actual).build();
    }
}
