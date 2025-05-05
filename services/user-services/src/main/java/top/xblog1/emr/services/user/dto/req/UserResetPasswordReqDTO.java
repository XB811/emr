package top.xblog1.emr.services.user.dto.req;

import lombok.Data;

/**
 *
 */
@Data
public class UserResetPasswordReqDTO {
    private String phone;
    private String password;
    private String code;
}
