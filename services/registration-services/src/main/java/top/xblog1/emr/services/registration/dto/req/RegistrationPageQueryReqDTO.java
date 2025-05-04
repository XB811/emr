package top.xblog1.emr.services.registration.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import top.xblog1.emr.framework.starter.convention.page.PageRequest;

import java.util.Date;
import java.util.List;

/**
 *
 */
@Data
public class RegistrationPageQueryReqDTO extends PageRequest {
    private List<Long> patientIds;
    private Long doctorId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate;
    private Integer isFinish;
    private Integer appointmentTime;
}
