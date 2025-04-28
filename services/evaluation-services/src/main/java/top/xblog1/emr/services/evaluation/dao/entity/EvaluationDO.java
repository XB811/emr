package top.xblog1.emr.services.evaluation.dao.entity;

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
@TableName("evaluation")
public class EvaluationDO extends BaseDO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long emrId;
    private String content;
}
