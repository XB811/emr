package top.xblog1.emr.services.booking.dto.req;

import lombok.Data;
import top.xblog1.emr.framework.starter.convention.page.PageRequest;

/**
 *
 */
@Data
public class BookingPageQueryReqDTO extends PageRequest {
    private Long doctorId;
    private String doctorName;
    private Boolean isAvailable;
}
