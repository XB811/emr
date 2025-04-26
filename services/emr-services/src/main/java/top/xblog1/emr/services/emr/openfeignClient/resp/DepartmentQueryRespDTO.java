package top.xblog1.emr.services.emr.openfeignClient.resp;

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
