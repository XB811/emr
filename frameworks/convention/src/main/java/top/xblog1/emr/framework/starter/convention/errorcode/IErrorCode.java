package top.xblog1.emr.framework.starter.convention.errorcode;

/**
 * 异常码接口
 */

public interface IErrorCode {

    /**
     * 错误码
     */
    String code();

    /**
     * 错误信息
     */
    String message();
}
