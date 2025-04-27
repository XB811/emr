package top.xblog1.emr.services.notice.dto.req;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class NoticeUpdateReqDTO {
    private Long id;
    private String adminName;
    private String title;
    private String content;
}
