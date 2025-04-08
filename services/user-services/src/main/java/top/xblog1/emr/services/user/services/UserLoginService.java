package top.xblog1.emr.services.user.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import top.xblog1.emr.services.user.dto.req.UserDeletionReqDTO;
import top.xblog1.emr.services.user.dto.req.UserLoginReqDTO;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserLoginRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserRegisterRespDTO;

/**
 * 用户登录服务层接口
 */

public interface UserLoginService {
    /**
    * 用户注册
    * @param requestParam
    * @return UserRegisterRespDTO
    */
    UserRegisterRespDTO register(@Valid UserRegisterReqDTO requestParam);

    /**
    * 注销用户
    * @param requestParam
    * @return
    */
    void deletion(@Valid UserDeletionReqDTO requestParam);

    /**
     * 查询用户名是否存在
     *
     * @param username
     * @param userType
     * @return Boolean
     */
    Boolean hasUsername(@NotEmpty String username, @NotEmpty String userType);

    /**
    * 用户登录
    * @param requestParam
    * @return UserLoginRespDTO
    */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 通过 Token 检查用户是否登录
     *
     * @param accessToken
     * @return UserLoginRespDTO
     */
    UserLoginRespDTO checkLogin(String accessToken);

    /**
     * 退出登录
     *
     * @param accessToken
     * @param userType
     * @return
     */
    void logout(String accessToken, @NotEmpty String userType);
}
