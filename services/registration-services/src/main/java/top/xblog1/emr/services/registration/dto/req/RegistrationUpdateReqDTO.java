package top.xblog1.emr.services.registration.dto.req;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class RegistrationUpdateReqDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Date appointmentDate;
    private Integer isFinish;
    private Integer appointmentTime;
}
