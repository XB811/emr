package top.xblog1.emr.services.notice.dto.req;

import lombok.Data;

/**
 *
 */
@Data
public class NoticeCreateReqDTO {
    private Long adminId;
    private String adminName;
    private String title;
    private String content;
}
