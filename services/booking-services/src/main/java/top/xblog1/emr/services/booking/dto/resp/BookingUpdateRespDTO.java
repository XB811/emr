package top.xblog1.emr.services.booking.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class BookingUpdateRespDTO {
    Long id;
    Long doctorId;
    String doctorName;
    Long availableTime;
    Boolean isAvailable;
    Date createTime;
    Date updateTime;
}
