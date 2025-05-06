package top.xblog1.emr.services.user.services.strategy.admin;


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
import top.xblog1.emr.framework.starter.common.enums.DelEnum;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.AbstractException;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.framework.starter.designpattern.chain.AbstractChainContext;
import top.xblog1.emr.framework.starter.user.core.UserContext;
import top.xblog1.emr.framework.starter.user.core.UserInfoDTO;
import top.xblog1.emr.framework.starter.user.toolkit.JWTUtil;
import top.xblog1.emr.services.user.common.constant.UserExecuteStrategyContant;
import top.xblog1.emr.services.user.common.enums.UserChainMarkEnum;
import top.xblog1.emr.services.user.dao.entity.AdminDO;
import top.xblog1.emr.services.user.dao.mapper.AdminMapper;
import top.xblog1.emr.services.user.dto.req.UserDeletionReqDTO;
import top.xblog1.emr.services.user.dto.req.UserLoginReqDTO;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserLoginRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserRegisterRespDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.strategy.AbstractUserExecuteStrategy;
import top.xblog1.emr.services.user.toolkit.PasswordEncryptUtil;
import top.xblog1.emr.services.user.toolkit.SessionIdUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.*;
import static top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum.*;

/**
 * 管理员登录策略
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminLoginStrategy extends AbstractUserExecuteStrategy {

    private final AbstractChainContext<UserRegisterReqDTO> abstractChainContext;
    private final RedissonClient redissonClient;
    private final AdminMapper adminMapper;
    private final DistributedCache distributedCache;

    public String mark() {
        return UserTypeEnum.ADMIN.code() + UserExecuteStrategyContant.USER_LOGIN_STRATEGY_SUFFIX;
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
     * 用户注册
     */
    public BaseUserDTO register(BaseUserDTO baseUserDTO) {
        //拆包
        UserRegisterReqDTO requestParam = baseUserDTO.getUserRegisterReqDTO();
        //从redis获取用户名
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        Integer username_suffix = Integer.valueOf(Objects.requireNonNull(instance.opsForValue().get(USER_REGISTER_USERNAME_ADMIN)));
        requestParam.setUsername(UserTypeEnum.ADMIN.code()+String.format("%08d", username_suffix));
        //username_suffix自增后存会redis
        instance.opsForValue().set(USER_REGISTER_USERNAME_ADMIN, String.valueOf(username_suffix+1));
        //数据校验
        abstractChainContext.handler(UserChainMarkEnum.USER_REGISTER_FILTER.name(), requestParam);
        //密码加密
        requestParam.setPassword(PasswordEncryptUtil.encryptPassword(requestParam.getPassword()));
        //获取用户名注册锁
        RLock lock = redissonClient.getLock(LOCK_ADMIN_REGISTER + requestParam.getUsername());
        //写入数据库
        boolean tryLock = lock.tryLock();
        if (!tryLock) {
            throw new ServiceException(HAS_USERNAME_NOTNULL);
        }
        try{
            //尝试把手机号写入redis
            if(Boolean.TRUE.equals(instance.opsForSet().isMember(USER_REGISTER_PHONE_ADMIN, requestParam.getPhone())))
                throw new ClientException(HAS_PHONE);
            instance.opsForSet().add(USER_REGISTER_PHONE_ADMIN, requestParam.getPhone());
                //尝试写入数据库
                try {
                    int inserted = adminMapper.insert(BeanUtil.convert(requestParam, AdminDO.class));
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
    * 用户注销
    */
    @Transactional(rollbackFor = Exception.class)
    public void deletion(BaseUserDTO baseUserDTO){
        //拆包
        UserDeletionReqDTO requestParam = baseUserDTO.getUserDeletionReqDTO();
        //只有root和当前用户可以注销所有用户
        String username = UserContext.getUsername();
        if (!Objects.equals(username, requestParam.getUsername())) {
            //如果登录账户和注销账户不一样，且登录用户不是root用户
            if(!Objects.equals(username,"root"))
                throw new ClientException("注销账号与登录账号不一致");
        }
        //如果要注销的账户是root，不可删除
        if(Objects.equals(requestParam.getUsername(),"root")){
            throw new ClientException("root用户不可删除");
        }
        RLock lock = redissonClient.getLock(USER_DELETION + requestParam.getUsername());
        boolean tryLock=lock.tryLock();
        if (!tryLock) {
            throw new ServiceException("用户注销失败");
        }
        try{
            //查询到用户并删除
            LambdaQueryWrapper<AdminDO> queryWrapper = Wrappers.lambdaQuery(AdminDO.class)
                    .eq(AdminDO::getUsername, requestParam.getUsername());
            AdminDO adminDO = adminMapper.selectOne(queryWrapper);
            if(adminDO==null)
                throw new ClientException("该用户id不存在");
            //更新 删除时间
            adminDO.setUpdateTime(null);
            adminMapper.updateById(adminDO);
            adminMapper.deleteById(adminDO.getId());
            //删除redis中已注册手机号
            StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
            instance.opsForSet().remove(USER_REGISTER_PHONE_ADMIN,adminDO.getPhone());
            /**
            * 删除登录信息
             * id到token的双向映射
            */
            String token = distributedCache.get(USER_LOGIN_ADMIN_TOKEN_PREFIX + adminDO.getId(), String.class);
            if (!Objects.isNull(token)) {
                distributedCache.delete(token);
                distributedCache.delete(USER_LOGIN_ADMIN_TOKEN_PREFIX + adminDO.getId());
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
        //根账户直接返回true
        if(Objects.equals(username,"root"))
            return BaseUserDTO.builder()
                    .hasUsername(true)
                    .build();
        //获取用户名后缀
        Integer usernameSuffix= Integer.valueOf(username.replace(UserTypeEnum.ADMIN.code(),""));
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
        //获取redis中最新的用户名
        Integer redisUsernameSuffix = Integer.valueOf(instance.opsForValue().get(USER_REGISTER_USERNAME_ADMIN));
        //如果查询的用户名小于redis中缓存的用户名，则存在，返回true
        return BaseUserDTO.builder()
                .hasUsername(usernameSuffix<redisUsernameSuffix)
                .build();
    }
    /**
    * 用户登录
    */
    public BaseUserDTO login(BaseUserDTO baseUserDTO) {
        //拆包
        UserLoginReqDTO requestParam = baseUserDTO.getUserLoginReqDTO();
        //根据username或者手机号分别查询，一个成功即可
        LambdaQueryWrapper<AdminDO> queryWrapperByUsername = Wrappers.lambdaQuery(AdminDO.class)
                .eq(AdminDO::getUsername, requestParam.getUsernameOrPhone());
        LambdaQueryWrapper<AdminDO> queryWrapperByPhone = Wrappers.lambdaQuery(AdminDO.class)
                .eq(AdminDO::getPhone, requestParam.getUsernameOrPhone());
        //查询
        AdminDO adminDO = Optional.ofNullable(adminMapper.selectOne(queryWrapperByUsername))
                .or(() -> Optional.ofNullable(adminMapper.selectOne(queryWrapperByPhone)))
                .orElseThrow(() -> new ClientException("用户名/手机号不存在"));
        //判断密码是否正确
        if(Objects.isNull(requestParam.getPassword())|| requestParam.getPassword().isEmpty()){
            throw new ClientException(PASSWORD_NOTNULL);
        }
        if(!PasswordEncryptUtil.verifyPassword(requestParam.getPassword(),adminDO.getPassword())){
            throw new ClientException("密码错误");
        }
        //构建返回值
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userId(String.valueOf(adminDO.getId()))
                .username(adminDO.getUsername())
                .realName(adminDO.getRealName())
                .userType(UserTypeEnum.ADMIN.code())
                .build();
        // String accessToken = JWTUtil.generateAccessToken(userInfoDTO);
        //更换token生成方式
        String accessToken = SessionIdUtil.generateAccessToken(userInfoDTO);
        UserLoginRespDTO actual = UserLoginRespDTO.builder()
                .userId(userInfoDTO.getUserId())
                .username(userInfoDTO.getUsername())
                .realName(userInfoDTO.getRealName())
                .userType(userInfoDTO.getUserType())
                .accessToken(accessToken)
                .build();
        //删除redis中可能缓存的token
        String tokenName=USER_LOGIN_ADMIN_TOKEN_PREFIX +adminDO.getId();
        if(distributedCache.get(tokenName,String.class)!=null){
            distributedCache.delete(distributedCache.get(tokenName,String.class));
            distributedCache.delete(tokenName);
        }

        //存入redis
        distributedCache.put(accessToken, JSON.toJSONString(actual), 30, TimeUnit.MINUTES);
        distributedCache.put(tokenName,accessToken,30, TimeUnit.MINUTES);
        //包装返回
        return BaseUserDTO.builder()
                .userLoginRespDTO(UserLoginRespDTO.builder().accessToken(accessToken).build()).build();
    }

}
