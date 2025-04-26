package top.xblog1.emr.services.emr.common.enums;


import lombok.AllArgsConstructor;
import top.xblog1.emr.framework.starter.convention.errorcode.IErrorCode;

/**
 *
 */
@AllArgsConstructor
public enum EmrErrorCodeEnum implements IErrorCode {
    REMOTE_CALL_FAIL("A003001","远程调用失败"),
    DELETE_FAIL("A003002","删除病历失败"),
    QUERY_FAIL("A003003","病历查询失败"),
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
