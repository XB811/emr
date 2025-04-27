package top.xblog1.emr.services.booking.dto.req;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class BookingUpdateReqDTO {
    Long id;
    Long doctorId;
    String doctorName;
    Long availableTime;
    Boolean isAvailable;
}
