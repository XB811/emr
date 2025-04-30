package top.xblog1.emr.services.user.common.enums;


import lombok.AllArgsConstructor;
import top.xblog1.emr.framework.starter.convention.errorcode.IErrorCode;

/**
 *
 */
@AllArgsConstructor
public enum UserErrorCodeEnum implements IErrorCode {
    ILLEGAL_TOKE("A100001","token不存在"),
    OTHER_CLIENTS_LOGGED_IN("A100002","其他客户端登录"),
    TOKEN_EXPIRED("A100003","Token过期"),
    ;
    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误提示消息
     */
    private final String message;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
