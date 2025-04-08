package top.xblog1.emr.services.user.dto.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.xblog1.emr.framework.starter.database.base.BaseDO;
import top.xblog1.emr.services.user.common.enums.UserOperationTypeEnum;
import top.xblog1.emr.services.user.dto.req.UserDeletionReqDTO;
import top.xblog1.emr.services.user.dto.req.UserLoginReqDTO;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;
import top.xblog1.emr.services.user.dto.req.UserUpdateReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserLoginRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserRegisterRespDTO;

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
    //用户id
    private Long id;
    //用户名
    private String username;
    // 用户名存在
    private Boolean hasUsername;
    //操作类型
    private UserOperationTypeEnum operationType;
}
