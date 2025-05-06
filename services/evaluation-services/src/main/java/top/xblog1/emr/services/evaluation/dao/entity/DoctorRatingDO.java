package top.xblog1.emr.services.evaluation.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("doctor_rating_view")
public class DoctorRatingDO {
    private Long doctorId;
    private Double averageRating;
}
