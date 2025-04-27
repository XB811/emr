package top.xblog1.emr.services.registration.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.xblog1.emr.framework.starter.database.base.BaseDO;

import java.util.Date;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("registration")
public class RegistrationDO extends BaseDO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Date appointmentDate;
    private Integer isFinish;
    private Integer appointmentTime;
}
