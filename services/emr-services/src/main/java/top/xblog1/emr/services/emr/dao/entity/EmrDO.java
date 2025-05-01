package top.xblog1.emr.services.emr.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.xblog1.emr.framework.starter.database.base.BaseDO;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("emr")
public class EmrDO extends BaseDO {
    private Long id;
    private Long patientId;
    private Long departmentId;
    private Long doctorId;

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

    @TableField(fill = FieldFill.INSERT)
    @TableLogic(value = "0", delval = "1")
    private Long delFlag;

}
