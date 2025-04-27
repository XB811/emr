package top.xblog1.emr.services.booking.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.xblog1.emr.framework.starter.database.base.BaseDO;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("booking")
public class BookingDO extends BaseDO {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long availableTime;
    private Boolean isAvailable;
}
