package top.xblog1.emr.services.booking.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class BookingQueryRespDTO {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long availableTime;
    private Boolean isAvailable;
    private Date createTime;
    private Date updateTime;
}
