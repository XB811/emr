package top.xblog1.emr.services.emr.dto.req;

import lombok.Data;
import top.xblog1.emr.framework.starter.convention.page.PageRequest;

/**
 *
 */
@Data
public class EmrPageQueryReqDTO extends PageRequest {
    private Long patientId;
    private Long departmentId;
    private Long doctorId;
    private String realName;

}
