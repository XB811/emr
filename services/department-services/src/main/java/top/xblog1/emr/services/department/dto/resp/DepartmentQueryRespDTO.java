package top.xblog1.emr.services.department.dto.resp;

import lombok.Builder;
import lombok.Data;

/**
 *
 */
@Data
public class DepartmentQueryRespDTO {
    String id;
    String code;
    String name;
    String detail;
    String address;
}
