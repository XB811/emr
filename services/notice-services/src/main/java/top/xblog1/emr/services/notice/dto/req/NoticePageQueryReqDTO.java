package top.xblog1.emr.services.notice.dto.req;

import lombok.Data;
import top.xblog1.emr.framework.starter.convention.page.PageRequest;

/**
 *
 */
@Data
public class NoticePageQueryReqDTO extends PageRequest {
    private Long adminId;
    private String adminName;
    private String title;
    private String content;
}
