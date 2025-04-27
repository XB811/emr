package top.xblog1.emr.services.registration.dto.req;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class RegistrationCreateReqDTO {
    private Long patientId;
    private Long doctorId;
    private Date appointmentDate;
    private Integer appointmentTime;
}
