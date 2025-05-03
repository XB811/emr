package top.xblog1.emr.services.user.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.services.user.dto.req.UpdatePasswordReqDTO;
import top.xblog1.emr.services.user.dto.req.UserPageQueryReqDTO;
import top.xblog1.emr.services.user.dto.req.UserUpdateReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryRespDTO;

/**
 * 用户信息管理服务层接口
 */

public interface UserService {
    /**
    * 根据id修改用户
    * @param requestParam 
    * @return 
    */
    void update(@Valid UserUpdateReqDTO requestParam);

    /**
    * 根据用户id和用户类型查询用户信息
    * @param id 
     * @param userType 
    * @return UserQueryRespDTO 
    */
    UserQueryRespDTO queryUserByIDAndUserType(@NotEmpty Long id, @NotEmpty String userType);

    /**
    * 根据用户id和用户类型查询用户无脱敏信息
    * @param id 
     * @param userType 
    * @return UserQueryActualRespDTO 
    */
    UserQueryActualRespDTO queryActualUserByIDAndUserType(@NotEmpty Long id, @NotEmpty String userType);

    void updatePassword(UpdatePasswordReqDTO requestParam, @NotEmpty String userType);

    PageResponse<UserQueryRespDTO> pageQuery(@Valid UserPageQueryReqDTO requestParam, @NotEmpty String userType);
}
