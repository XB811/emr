package top.xblog1.emr.services.department.dto.req;

import lombok.Data;

/**
 *
 */
@Data
public class DepartmentUpdateReqDTO {
    String id;
    String code;
    String name;
    String detail;
    String address;
}
