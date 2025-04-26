package top.xblog1.emr.services.department.dto.req;

import lombok.Data;

/**
 * 新增部门请求实体
 */
@Data
public class DepartmentInsertReqDTO {
    String code;
    String name;
    String detail;
    String address;
}
