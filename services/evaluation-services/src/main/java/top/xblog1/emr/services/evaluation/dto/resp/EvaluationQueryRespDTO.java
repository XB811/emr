package top.xblog1.emr.services.evaluation.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class EvaluationQueryRespDTO {
    private String id;
    private String patientId;
    private String doctorId;
    private String emrId;
    private String content;
    private Integer rating;
    private Date createTime;
    private Date updateTime;
}
