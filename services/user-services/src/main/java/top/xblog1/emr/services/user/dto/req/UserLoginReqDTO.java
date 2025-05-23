package top.xblog1.emr.services.user.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginReqDTO {
    /**
     * 用户名
     */
    private String usernameOrPhone;

    /**
     * 密码
     */
    private String password;
    /**
    * 用户类型
    */
    private String userType;
}
