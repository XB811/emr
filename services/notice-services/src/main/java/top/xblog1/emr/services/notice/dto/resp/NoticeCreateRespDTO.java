package top.xblog1.emr.services.notice.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class NoticeCreateRespDTO {
    private Long id;
    private Long adminId;
    private String adminName;
    private String title;
    private String content;
    private Date createTime;
    private Date updateTime;
}
