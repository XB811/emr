package top.xblog1.emr.services.notice.services;

import jakarta.validation.Valid;
import top.xblog1.emr.services.notice.dto.req.NoticeCreateReqDTO;
import top.xblog1.emr.services.notice.dto.req.NoticeUpdateReqDTO;
import top.xblog1.emr.services.notice.dto.resp.NoticeCreateRespDTO;
import top.xblog1.emr.services.notice.dto.resp.NoticeQueryRespDTO;
import top.xblog1.emr.services.notice.dto.resp.NoticeUpdateRespDTO;

/**
 *
 */

public interface NoticeServices {
    NoticeCreateRespDTO create(NoticeCreateReqDTO requestParam);

    void delete(@Valid Long id);

    NoticeUpdateRespDTO update(NoticeUpdateReqDTO requestParam);

    NoticeQueryRespDTO queryById(@Valid Long id);
}
