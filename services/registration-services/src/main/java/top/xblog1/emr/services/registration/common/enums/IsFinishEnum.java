package top.xblog1.emr.services.registration.common.enums;


/**
 *
 */
public enum IsFinishEnum {

    /**
     * 未完成状态
     */
    NOT_FINISH(0),

    /**
     * 一级完成状态
     */
    FINISHED(1);

    private final Integer statusCode;

    IsFinishEnum(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer code() {
        return this.statusCode;
    }

    public String strCode() {
        return String.valueOf(this.statusCode);
    }

    @Override
    public String toString() {
        return strCode();
    }
}
