package top.xblog1.emr.services.registration.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class RegistrationQueryRespDTO {
    private String id;
    private String patientId;
    private String doctorId;
    private Date appointmentDate;
    private Integer isFinish;
    private Integer appointmentTime;
}
