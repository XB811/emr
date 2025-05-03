package top.xblog1.emr.services.evaluation.dto.req;

import lombok.Data;
import top.xblog1.emr.framework.starter.convention.page.PageRequest;

/**
 *
 */
@Data
public class EvaluationPageQueryReqDTO extends PageRequest {
    private Long patientId;
    private Long doctorId;
}
