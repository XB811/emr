package top.xblog1.emr.services.user.dto.resp;

import lombok.Data;

/**
 *
 */
@Data
public class UserQueryActualRespDTO {
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 用户手机号
     */
    private String phone;
    /**
     * 用户性别
     */
    private Boolean gender;
    /**
     * 部门
     */
    private Long departmentId;
    /**
     * 职称
     */
    private String title;
    /**
     * 擅长
     */
    private String specialty;
    /**
     * 身份证
     */
    private String idCard;
}
