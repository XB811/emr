package top.xblog1.emr.services.user.dto.req;

import lombok.Data;

/**
 *
 */
@Data
public class UpdatePasswordReqDTO {
    private String oldPassword; // 旧密码
    private String newPassword; // 新密码
    private String confirmPassword;// 确认密码字段
}
