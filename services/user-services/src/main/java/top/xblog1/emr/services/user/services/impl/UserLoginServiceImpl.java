package top.xblog1.emr.services.user.services.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.xblog1.emr.framework.starter.cache.DistributedCache;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.designpattern.strategy.AbstractStrategyChoose;
import top.xblog1.emr.framework.starter.user.core.UserContext;
import top.xblog1.emr.services.user.common.enums.UserOperationTypeEnum;
import top.xblog1.emr.services.user.dto.req.UserDeletionReqDTO;
import top.xblog1.emr.services.user.dto.req.UserLoginReqDTO;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserLoginRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserRegisterRespDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;
import top.xblog1.emr.services.user.services.UserLoginService;

import static top.xblog1.emr.services.user.common.constant.RedisKeyConstant.USER_LOGIN_TOKEN_PREFIX;
import static top.xblog1.emr.services.user.common.constant.UserExecuteStrategyContant.USER_LOGIN_STRATEGY_SUFFIX;
import static top.xblog1.emr.services.user.common.enums.UserOperationTypeEnum.USER_LOGIN;

/**
 * 用户登录实现
 */
@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {
    private final AbstractStrategyChoose strategyChoose;
    private final DistributedCache distributedCache;
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
    public UserLoginRespDTO checkLogin(String accessToken) {
        //直接查询token是否存在，不再调用策略
        return distributedCache.get(accessToken,UserLoginRespDTO.class);
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
                userId = distributedCache.get(accessToken, UserLoginRespDTO.class).getUserId();
                distributedCache.delete(USER_LOGIN_TOKEN_PREFIX+userType+":"+userId);
                distributedCache.delete(accessToken);
            } catch (Exception e) {
                throw new ClientException("token校验失败");
            }

        }
    }
}
