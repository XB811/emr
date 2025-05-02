package top.xblog1.emr.services.emr.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class EmrCreateRespDTO {
    private String id;
    private String patientId;
    private String departmentId;
    private String doctorId;

    private String realName;
    private Boolean gender;
    private Integer age;

    private String doctorName;
    private String departmentCode;
    private String departmentName;

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
    private Date createTime;
    private Date updateTime;
}
