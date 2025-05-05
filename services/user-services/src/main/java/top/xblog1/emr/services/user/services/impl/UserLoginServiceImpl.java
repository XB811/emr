package top.xblog1.emr.services.user.services.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.xblog1.emr.framework.starter.cache.DistributedCache;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.designpattern.strategy.AbstractStrategyChoose;
import top.xblog1.emr.framework.starter.user.core.UserInfoDTO;
import top.xblog1.emr.services.user.common.enums.UserOperationTypeEnum;
import top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum;
import top.xblog1.emr.services.user.dto.req.UserDeletionReqDTO;
import top.xblog1.emr.services.user.dto.req.UserLoginReqDTO;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserInfoQueryByTokenRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserLoginRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserRegisterRespDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.UserLoginService;
import top.xblog1.emr.services.user.toolkit.SmsUtil;

import java.util.concurrent.TimeUnit;

import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.USER_LOGIN_PHONE_VERIFY_CODE_PREFIX;
import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.USER_LOGIN_TOKEN_PREFIX;
import static top.xblog1.emr.services.user.common.constant.UserExecuteStrategyContant.USER_LOGIN_STRATEGY_SUFFIX;
import static top.xblog1.emr.services.user.common.enums.UserErrorCodeEnum.ILLEGAL_TOKE;
import static top.xblog1.emr.services.user.common.enums.UserErrorCodeEnum.TOKEN_EXPIRED;
import static top.xblog1.emr.services.user.common.enums.UserOperationTypeEnum.USER_LOGIN;

/**
 * 用户登录实现
 */
@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {
    private final AbstractStrategyChoose strategyChoose;
    private final DistributedCache distributedCache;
    private final StringRedisTemplate stringRedisTemplate;
    /**
    * 用户注册
    * @param requestParam
    * @return UserRegisterRespDTO 
    */
    @Override
    public UserRegisterRespDTO register(UserRegisterReqDTO requestParam) {
        //将请求参数封装到BaseUserDTO中
        BaseUserDTO request =BaseUserDTO.builder()
                                .userRegisterReqDTO(requestParam)
                                .operationType(UserOperationTypeEnum.USER_REGISTER)
                                .build();
        /**
        * 注册什么用户，就调用哪个策略注册
        * 是否能够注册由gateway进行权限验证
        */
        BaseUserDTO response= strategyChoose
                .chooseAndExecuteResp(requestParam.getUserType()+USER_LOGIN_STRATEGY_SUFFIX,
                        request);
        return response.getUserRegisterRespDTO();
    }

    /**
    * 用户注销
    * @param requestParam 
    * @return 
    */
    @Override
    public void deletion(UserDeletionReqDTO requestParam) {
        BaseUserDTO request = BaseUserDTO.builder()
                .userDeletionReqDTO(requestParam)
                .operationType(UserOperationTypeEnum.USER_DELETION)
                .build();
        /**
         * 注销什么用户，就调用哪个策略注册
         * 是否能够注册由gateway进行权限验证
         */
        strategyChoose.chooseAndExecute(requestParam.getUserType()+USER_LOGIN_STRATEGY_SUFFIX,
                request);
    }
    
    /**
     * 查询用户名是否存在
     *
     * @param username
     * @param userType
     * @return Boolean
     */
    @Override
    public Boolean hasUsername(String username, @NotEmpty String userType) {
        BaseUserDTO request = BaseUserDTO.builder()
                .username(username)
                .operationType(UserOperationTypeEnum.USER_HAS_USERNAME)
                .build();
        BaseUserDTO response= strategyChoose
                        .chooseAndExecuteResp(userType+USER_LOGIN_STRATEGY_SUFFIX,
                        request);
        return response.getHasUsername();
    }

    /**
    * 用户登录
    * @param requestParam 
    * @return UserLoginRespDTO 
    */
    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        BaseUserDTO request = BaseUserDTO.builder()
                .userLoginReqDTO(requestParam)
                .operationType(USER_LOGIN)
                .build();
        /**
         * 登录什么用户，就调用哪个策略注册
         * 是否能够注册由gateway进行权限验证
         */
        BaseUserDTO response= strategyChoose
                .chooseAndExecuteResp(requestParam.getUserType()+USER_LOGIN_STRATEGY_SUFFIX,
                        request);
        return response.getUserLoginRespDTO();
    }

    /**
     * 通过 Token 检查用户是否登录
     *
     * @param accessToken
     * @return UserLoginRespDTO
     */
    @Override
    public Boolean checkLogin(String accessToken) {
        //直接查询token是否存在，不再调用策略
        UserInfoDTO userInfoDTO = JSON.parseObject(distributedCache.get(accessToken, String.class),UserInfoDTO.class);
        return userInfoDTO != null;
    }

    /**
     * 退出登录
     *
     * @param accessToken
     * @param userType
     * @return
     */
    @Override
    public void logout(String accessToken, @NotEmpty String userType) {
        //直接根据token查询，不再调用策略
        if (StrUtil.isNotBlank(accessToken)) {
            String userId = null;
            try {
                UserInfoDTO userInfoDTO = JSON.parseObject(distributedCache.get(accessToken, String.class),UserInfoDTO.class);
                if(userInfoDTO==null) {
                    throw new ClientException(TOKEN_EXPIRED);
                }
                userId =userInfoDTO.getUserId();
                distributedCache.delete(USER_LOGIN_TOKEN_PREFIX+userType+":"+userId);
                distributedCache.delete(accessToken);
            } catch (Exception e) {
                throw new ClientException(ILLEGAL_TOKE);
            }

        }
    }

    @Override
    public UserInfoQueryByTokenRespDTO getUserInfoByToken(String token) {
        String s = stringRedisTemplate.opsForValue().get(token);
        return JSON.parseObject(s,UserInfoQueryByTokenRespDTO.class);
//        return BeanUtil.convert(distributedCache.get(token, UserInfoDTO.class), UserInfoQueryByTokenRespDTO.class);
    }

    @Override
    public void getVerifyCode(@Valid String phone, @NotEmpty String userType) {
        if(StrUtil.isBlank(phone)) {
            throw new ClientException("手机号不能为空");
        }
        if(!Validator.isMobile(phone))
            throw new ClientException(UserRegisterErrorCodeEnum.PHONE_PATTERN_ERROR);
        if(StrUtil.isBlank(userType)) {
            throw new ClientException("用户类型不能为空");
        }
        if(!userType.equals("admin")&&!userType.equals("doctor")&&!userType.equals("patient")){
            throw new ClientException("用户类型错误");
        }
        //先从缓存获取验证码，如果存在，直接返回
        String code = distributedCache.get(USER_LOGIN_PHONE_VERIFY_CODE_PREFIX + userType+':'+phone, String.class);
        if(code!=null) {
            return;
        }
        //生成6位随机验证码
        code = RandomUtil.randomNumbers(6);
        //将验证码存入缓存中，并设置10分钟时长
        distributedCache.put(USER_LOGIN_PHONE_VERIFY_CODE_PREFIX + userType+":"+phone, code,10, TimeUnit.MINUTES);
        //发送验证码
        SmsUtil.sendSms(phone,code);
    }
}
