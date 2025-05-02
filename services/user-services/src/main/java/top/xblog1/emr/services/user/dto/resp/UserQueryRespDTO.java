package top.xblog1.emr.services.user.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import top.xblog1.emr.services.user.serialize.IdCardDesensitizationSerializer;
import top.xblog1.emr.services.user.serialize.PhoneDesensitizationSerializer;

/**
 *
 */
@Data
public class UserQueryRespDTO {
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
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;
    /**
     * 用户性别
     */
    private Boolean gender;
    /**
     * 部门
     */
    private String departmentId;
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
    @JsonSerialize(using = IdCardDesensitizationSerializer.class)
    private String idCard;
}
