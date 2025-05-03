package top.xblog1.emr.services.registration.services;

import jakarta.validation.Valid;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.services.registration.dto.req.RegistrationCreateReqDTO;
import top.xblog1.emr.services.registration.dto.req.RegistrationPageQueryReqDTO;
import top.xblog1.emr.services.registration.dto.req.RegistrationUpdateReqDTO;
import top.xblog1.emr.services.registration.dto.resp.RegistrationCreateRespDTO;
import top.xblog1.emr.services.registration.dto.resp.RegistrationQueryRespDTO;
import top.xblog1.emr.services.registration.dto.resp.RegistrationUpdateRespDTO;

/**
 *
 */

public interface RegistrationServices {
    RegistrationCreateRespDTO create(RegistrationCreateReqDTO requestParam);

    void delete(@Valid Long id);

    RegistrationUpdateRespDTO update(RegistrationUpdateReqDTO requestParam);

    RegistrationQueryRespDTO queryById(Long id);

    void finish(@Valid Long id);

    PageResponse<RegistrationQueryRespDTO> pageQuery(RegistrationPageQueryReqDTO requestParam);
}
