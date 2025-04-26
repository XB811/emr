package top.xblog1.emr.services.emr.services;

import top.xblog1.emr.services.emr.dto.req.EmrCreateReqDTO;
import top.xblog1.emr.services.emr.dto.req.EmrUpdateReqDTO;
import top.xblog1.emr.services.emr.dto.resp.EmrCreateRespDTO;
import top.xblog1.emr.services.emr.dto.resp.EmrQueryRespDTO;
import top.xblog1.emr.services.emr.dto.resp.EmrUpdateRespDTO;

/**
 *
 */

public interface EmrServices {
    EmrCreateRespDTO create(EmrCreateReqDTO requestParam);

    void delete(Long id);

    EmrUpdateRespDTO update(EmrUpdateReqDTO requestParam);

    EmrQueryRespDTO queryById(Long id);
}
