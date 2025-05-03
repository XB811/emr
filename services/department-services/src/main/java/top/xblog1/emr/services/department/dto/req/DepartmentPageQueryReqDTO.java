package top.xblog1.emr.services.department.dto.req;

import lombok.Data;
import top.xblog1.emr.framework.starter.convention.page.PageRequest;

/**
 *
 */
@Data
public class DepartmentPageQueryReqDTO extends PageRequest {
    String code;
    String name;
    String address;
}
