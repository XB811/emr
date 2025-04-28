package top.xblog1.emr.services.evaluation.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class EvaluationQueryRespDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long emrId;
    private String content;
    private Date createTime;
    private Date updateTime;
}
