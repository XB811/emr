package top.xblog1.emr.services.evaluation.dto.req;

import lombok.Data;

/**
 *
 */
@Data
public class EvaluationUpdateReqDTO {
    private Long Id;
    private Long patientId;
    private Long doctorId;
    private Long emrId;
    private String content;
}
