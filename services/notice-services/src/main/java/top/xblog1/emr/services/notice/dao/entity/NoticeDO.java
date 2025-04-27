package top.xblog1.emr.services.notice.dao.entity;

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
@TableName("notice")
public class NoticeDO extends BaseDO {
    private Long id;
    private Long adminId;
    private String adminName;
    private String title;
    private String content;

}
