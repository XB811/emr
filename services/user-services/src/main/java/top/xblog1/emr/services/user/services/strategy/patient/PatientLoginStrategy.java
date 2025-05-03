package top.xblog1.emr.services.user.services.strategy.patient;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
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
import top.xblog1.emr.services.user.dao.entity.PatientDO;
import top.xblog1.emr.services.user.dao.entity.PatientPhoneReuseDO;
import top.xblog1.emr.services.user.dao.mapper.PatientMapper;
import top.xblog1.emr.services.user.dao.mapper.PatientPhoneReuseMapper;
import top.xblog1.emr.services.user.dto.req.UserDeletionReqDTO;
import top.xblog1.emr.services.user.dto.req.UserLoginReqDTO;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserLoginRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserRegisterRespDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.strategy.AbstractUserExecuteStrategy;
import top.xblog1.emr.services.user.toolkit.PasswordEncryptUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.*;
import static top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum.*;
import static top.xblog1.emr.services.user.toolkit.UserReuseUtil.hashShardingIdx;

/**
 * 患者登录策略
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PatientLoginStrategy extends AbstractUserExecuteStrategy {

    private final AbstractChainContext<UserRegisterReqDTO> abstractChainContext;
    private final RedissonClient redissonClient;
    private final DistributedCache distributedCache;
    private final RBloomFilter<String> patientRegisterUsernameCachePenetrationBloomFilter;
    private final RBloomFilter<String> patientRegisterPhoneCachePenetrationBloomFilter;
    private final PatientMapper patientMapper;
    private final PatientPhoneReuseMapper patientPhoneReuseMapper;

    public String mark(){
        return UserTypeEnum.PATIENT.code()+ UserExecuteStrategyContant.USER_LOGIN_STRATEGY_SUFFIX;
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

    /**
    用户注册
     */
    public BaseUserDTO register(BaseUserDTO baseUserDTO) {
        //拆包
        UserRegisterReqDTO requestParam = baseUserDTO.getUserRegisterReqDTO();
        //数据校验
        abstractChainContext.handler(UserChainMarkEnum.USER_REGISTER_FILTER.name(), requestParam);
        //判断用户名和手机号是否重复
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        if(patientRegisterUsernameCachePenetrationBloomFilter.contains(requestParam.getUsername())){
            throw new ClientException("用户名已存在");
        }
        String phone =requestParam.getPhone();
        if(patientRegisterPhoneCachePenetrationBloomFilter.contains(requestParam.getPhone())){
            //不在可复用分片中
            if(Boolean.FALSE.equals(instance.opsForSet().isMember(PATIENT_REGISTER_PHONE_REUSE_SHARDING + hashShardingIdx(phone), phone)) )
                throw new ClientException("手机号已存在");
        }
        //密码加密
        requestParam.setPassword(PasswordEncryptUtil.encryptPassword(requestParam.getPassword()));
        //写入数据库
        RLock lock = redissonClient.getLock(LOCK_PATIENT_REGISTER + requestParam.getUsername());
        boolean tryLock = lock.tryLock();
        if (!tryLock) {
            throw new ServiceException(HAS_USERNAME_NOTNULL);
        }
        try {
            try {
                int insert = patientMapper.insert(BeanUtil.convert(requestParam, PatientDO.class));
                if(insert <1){
                    throw new ServiceException(USER_REGISTER_FAIL);
                }
            } catch (DuplicateKeyException e) {
                log.error("用户{} 重复注册", requestParam);
                throw new ServiceException(HAS_USERNAME_NOTNULL);
            }
            //写入布隆过滤器和复用表
            patientPhoneReuseMapper.delete(Wrappers.update(PatientPhoneReuseDO.builder()
                                        .phone(phone)
                                        .build()));
            instance.opsForSet().remove(PATIENT_REGISTER_PHONE_REUSE_SHARDING + hashShardingIdx(phone), phone);
            patientRegisterUsernameCachePenetrationBloomFilter.add(requestParam.getUsername());
            patientRegisterPhoneCachePenetrationBloomFilter.add(requestParam.getPhone());
        }catch (AbstractException ex){
            throw  ex;
        }finally {
            lock.unlock();
        }
        UserRegisterRespDTO response = BeanUtil.convert(requestParam, UserRegisterRespDTO.class);
        return BaseUserDTO.builder()
                .userRegisterRespDTO(response)
                .build();
    }

    /**
     * 用户注销
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletion(BaseUserDTO baseUserDTO) {
        //获取requestParam
        UserDeletionReqDTO requestParam = baseUserDTO.getUserDeletionReqDTO();
        RLock lock =redissonClient.getLock(PATIENT_DELETION+requestParam.getUsername());
        boolean tryLock=lock.tryLock();
        if (!tryLock) {
            throw new ServiceException("用户注销失败");
        }
        try{
            //查询到用户并删除
            LambdaQueryWrapper<PatientDO> queryWrapper = Wrappers.lambdaQuery(PatientDO.class)
                    .eq(PatientDO::getUsername, requestParam.getUsername());
            PatientDO patientDO = patientMapper.selectOne(queryWrapper);
            if(patientDO==null)
                throw new ClientException("该用户id不存在");
            //删除数据库
            patientDO.setUpdateTime(null);
            patientMapper.updateById(patientDO);
            patientMapper.deleteById(patientDO.getId());
            //手机号复用
            String phone =patientDO.getPhone();
            patientPhoneReuseMapper.insert(PatientPhoneReuseDO.builder()
                    .phone(phone)
                    .build());
            StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
            instance.opsForSet().add(PATIENT_REGISTER_PHONE_REUSE_SHARDING+hashShardingIdx(phone), phone);
            // 删除登录token
            String token =distributedCache.get(USER_LOGIN_PATIEN_TOKEN_PREFIX +patientDO.getId(),String.class);
            if(!Objects.isNull(token)){
                distributedCache.delete(token);
                distributedCache.delete(USER_LOGIN_PATIEN_TOKEN_PREFIX +patientDO.getId());
            }

        }finally {
            lock.unlock();
        }
    }


    public BaseUserDTO hasName(BaseUserDTO baseUserDTO){
        String username = baseUserDTO.getUsername();
        BaseUserDTO response = new BaseUserDTO();
        if(patientRegisterUsernameCachePenetrationBloomFilter.contains(username)){
            response.setHasUsername(true);
        }else{
            response.setHasUsername(false);
        }
        return response;
    }
    /**
    * 用户登录
    */
    public BaseUserDTO login(BaseUserDTO baseUserDTO){
        UserLoginReqDTO requestParam = baseUserDTO.getUserLoginReqDTO();
        //根据username或者手机号分别查询，一个成功即可
        LambdaQueryWrapper<PatientDO> queryWrapperByUsername = Wrappers.lambdaQuery(PatientDO.class)
                .eq(PatientDO::getUsername, requestParam.getUsernameOrPhone());
        LambdaQueryWrapper<PatientDO> queryWrapperByPhone = Wrappers.lambdaQuery(PatientDO.class)
                .eq(PatientDO::getPhone, requestParam.getUsernameOrPhone());
        //查询
        PatientDO patientDO = Optional.ofNullable(patientMapper.selectOne(queryWrapperByUsername))
                .or(()-> Optional.ofNullable(patientMapper.selectOne(queryWrapperByPhone)))
                .orElseThrow(()-> new ClientException("用户名/手机号不存在"));
        //判断密码是否正确
        if(Objects.isNull(requestParam.getPassword())|| requestParam.getPassword().isEmpty()){
            throw new ClientException(PASSWORD_NOTNULL);
        }
        if(!PasswordEncryptUtil.verifyPassword(requestParam.getPassword(),patientDO.getPassword())){
            throw new ClientException("密码错误");
        }
        //构建返回值
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userId(String.valueOf(patientDO.getId()))
                .username(patientDO.getUsername())
                .realName(patientDO.getRealName())
                .userType(UserTypeEnum.PATIENT.code())
                .build();
        String accessToken = JWTUtil.generateAccessToken(userInfoDTO);
        UserLoginRespDTO actual = UserLoginRespDTO.builder()
                .userId(userInfoDTO.getUserId())
                .username(userInfoDTO.getUsername())
                .realName(userInfoDTO.getRealName())
                .userType(userInfoDTO.getUserType())
                .accessToken(accessToken)
                .build();
        //删除缓存中可能存在的之前登录的token
        String tokenName= USER_LOGIN_PATIEN_TOKEN_PREFIX +patientDO.getId();
        if(distributedCache.get(tokenName,String.class)!=null){
            distributedCache.delete(distributedCache.get(tokenName,String.class));
            distributedCache.delete(tokenName);
        }
        //存入缓存
        distributedCache.put(accessToken, JSON.toJSONString(actual), 30, TimeUnit.MINUTES);
        distributedCache.put(tokenName,accessToken,30, TimeUnit.MINUTES);
        //包装返回
        return BaseUserDTO.builder()
                .userLoginRespDTO(UserLoginRespDTO.builder().accessToken(accessToken).build()).build();
    }
}
