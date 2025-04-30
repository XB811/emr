package top.xblog1.emr.services.user.dto.resp;

import lombok.Data;

/**
 *
 */
@Data
public class UserRegisterRespDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;
}
