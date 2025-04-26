package top.xblog1.emr.services.department.common.enums;


import lombok.AllArgsConstructor;
import top.xblog1.emr.framework.starter.convention.errorcode.IErrorCode;

/**
 * 部门创建异常码
 */
@AllArgsConstructor
public enum DepartmentCreateErrorCodeEnum implements IErrorCode {

    DEPARTMENT_CREATE_FAIL("A002000","部门创建失败"),
    NAME_NOTNULL("A002001","科室名不能为空"),
    DETAIL_NOTNULL("A002002","科室介绍不能为空"),
    ADDRESS_NOTNULL("A002003","科室地址不能为空"),
    HAS_NAME("A002004","科室名已经存在"),
    CODE_NULL("A002005","科室编码不能为空"),
    DEPARTMENT_DELETE_FAIL("A002006","科室删除失败"),
    ID_NOTNULL("A002007","ID不存在"),
    CANNOT_FIND_DEPARTMENT("A002008","科室查询失败"),
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
