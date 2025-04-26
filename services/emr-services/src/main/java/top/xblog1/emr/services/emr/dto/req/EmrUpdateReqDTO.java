package top.xblog1.emr.services.emr.dto.req;

import lombok.Data;

/**
 *
 */
@Data
public class EmrUpdateReqDTO {
    private Long id;
    private Long patientId;
    private Long departmentId;
    private Long doctorId;


    /*
    主诉/病情关键信息
     */
    private String content;
    /*
    现病史
     */
    private String presentHistory;
    /*
    既往史
     */
    private String pastHistory;
    /*
    药敏史
     */
    private String allergyHistory;
    /*
    诊断
     */
    private String diagnosis;
    /*
    治疗方案
     */
    private String treatmentPlan;
    /*
    医嘱
     */
    private String doctorAdvice;
}
