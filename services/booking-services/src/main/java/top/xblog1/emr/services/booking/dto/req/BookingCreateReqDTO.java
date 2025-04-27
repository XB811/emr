package top.xblog1.emr.services.booking.dto.req;

import lombok.Data;

/**
 *
 */
@Data
public class BookingCreateReqDTO {
    Long doctorId;
    Long availableTime;
    Boolean isAvailable;
}
