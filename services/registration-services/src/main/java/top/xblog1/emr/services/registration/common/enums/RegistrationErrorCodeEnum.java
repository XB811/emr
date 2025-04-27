package top.xblog1.emr.services.registration.common.enums;


import lombok.AllArgsConstructor;
import top.xblog1.emr.framework.starter.convention.errorcode.IErrorCode;

/**
 *
 */
@AllArgsConstructor
public enum RegistrationErrorCodeEnum implements IErrorCode {
    REMOTE_CALL_FAIL("B005001","远程调用失败"),
    DOCTOR_ID_NOTNULL("A005002","医生ID不能为空"),
    ID_NOTNULL("A005003","预约时间ID不能为空"),
    BOOKING_NOT_FOUNd("B005004","预约时间未找到"),
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
