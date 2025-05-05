package top.xblog1.emr.services.user.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 *
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum UserOperationTypeEnum {
    USER_REGISTER("register","用户注册"),
    USER_DELETION("deletion", "用户注销"),
    USER_HAS_USERNAME("hasName","用户名存在"),
    USER_LOGIN("login","用户登录"),
    USER_CHECK_LOGIN("checkLogin","根据token检查用户是否登录"),
    USER_LOGOUT("logout","退出登录"),
    USER_UPDATE("update","用户信息更新"),
    USER_QUERY_USER_BY_ID("queryUserByID","根据用户id查询用户信息"),
    USER_QUERY_ACTUAL_USER_BY_ID("queryActualUserByID","根据用户id查询用户无脱敏信息"),
    USER_PASSWORD_UPDATE("updatePassword","更新当前登录用户密码"),
    USER_PAGE_QUERY("pageQuery","分页查询"),
    USER_QUERY_ALL("queryAll","查询所有用户"),
    USER_RESET_PASSWORD("resetPassword","使用手机号重置密码"),
    ;
    private final String code;
    private final String desc;

}
