package top.xblog1.emr.services.evaluation.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class EvaluationCreateRespDTO {
    private String id;
    private String patientId;
    private String doctorId;
    private String emrId;
    private String content;
    private Date createTime;
    private Date updateTime;
}
