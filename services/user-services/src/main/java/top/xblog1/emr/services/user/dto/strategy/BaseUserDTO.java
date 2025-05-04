package top.xblog1.emr.services.user.dto.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.database.base.BaseDO;
import top.xblog1.emr.services.user.common.enums.UserOperationTypeEnum;
import top.xblog1.emr.services.user.dto.req.*;
import top.xblog1.emr.services.user.dto.resp.UserLoginRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserRegisterRespDTO;

import java.util.List;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseUserDTO {
    //八种请求/返回参数
    private UserDeletionReqDTO userDeletionReqDTO;
    private UserLoginReqDTO userLoginReqDTO;
    private UserRegisterReqDTO userRegisterReqDTO;
    private UserUpdateReqDTO userUpdateReqDTO;
    private UserLoginRespDTO userLoginRespDTO;
    private UserQueryActualRespDTO userQueryActualRespDTO;
    private UserRegisterRespDTO userRegisterRespDTO;
    private UpdatePasswordReqDTO updatePasswordReqDTO;
    private UserQueryRespDTO userQueryRespDTO;
    private UserPageQueryReqDTO userPageQueryReqDTO;
    private PageResponse<UserQueryRespDTO> userPageQueryRespDTO;
    private List<UserQueryRespDTO> userQueryRespDTOList;
    //用户id
    private Long id;
    //用户名
    private String username;
    // 用户名存在
    private Boolean hasUsername;
    //操作类型
    private UserOperationTypeEnum operationType;
}
