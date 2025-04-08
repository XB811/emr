package top.xblog1.emr.framework.starter.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户类型常量
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum UserTypeEnum {
    ADMIN("admin", "管理员"),
    DOCTOR("doctor", "医生"),
    PATIENT("patient", "患者"),
    GUEST("guest","未登录用户");
    private final String code;
    private final String desc;
}
