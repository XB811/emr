package top.xblog1.emr.services.user.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.xblog1.emr.framework.starter.database.base.BaseDO;

/**
 * 患者手机号复用
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("patient_phone_reuse")
public class PatientPhoneReuseDO extends BaseDO {
    private Long id;
    /**
     * 手机号
     */
    private String phone;
}
