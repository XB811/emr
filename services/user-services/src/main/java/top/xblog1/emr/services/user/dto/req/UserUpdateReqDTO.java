package top.xblog1.emr.services.user.dto.req;

import lombok.Data;

/**
 * 用户信息更新请求实体
 */
@Data
public class UserUpdateReqDTO {
    /**
     * 用户ID 不可更新
     */
    private String id;
    /**
    *  用户名 不可更新
    */
    private String username;
    /**
     * 密码
     */
    private String password;
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
    private String IdCard;
}
