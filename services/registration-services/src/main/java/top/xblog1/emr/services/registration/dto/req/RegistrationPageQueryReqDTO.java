package top.xblog1.emr.services.registration.dto.req;

import lombok.Data;
import top.xblog1.emr.framework.starter.convention.page.PageRequest;

import java.util.Date;

/**
 *
 */
@Data
public class RegistrationPageQueryReqDTO extends PageRequest {
    private Long patientId;
    private Long doctorId;
    private Date appointmentDate;
    private Integer isFinish;
    private Integer appointmentTime;
}
