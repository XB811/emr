package top.xblog1.emr.services.registration.dto.resp;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class RegistrationUpdateRespDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Date appointmentDate;
    private Integer isFinish;
    private Integer appointmentTime;
    private Date createTime;
    private Date updateTime;
}
