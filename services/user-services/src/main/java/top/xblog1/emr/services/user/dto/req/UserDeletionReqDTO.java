package top.xblog1.emr.services.user.dto.req;

import lombok.Builder;
import lombok.Data;

/**
 * 用户注销请求参数
 */
@Data
@Builder
public class UserDeletionReqDTO {
    /**
     * 用户名
     */
    private String username;
    /**
    * 用户类型
    */
    private String userType;
}
