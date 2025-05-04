package top.xblog1.emr.services.evaluation.services;

import jakarta.validation.Valid;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.services.evaluation.dto.req.EvaluationCreateReqDTO;
import top.xblog1.emr.services.evaluation.dto.req.EvaluationPageQueryReqDTO;
import top.xblog1.emr.services.evaluation.dto.req.EvaluationUpdateReqDTO;
import top.xblog1.emr.services.evaluation.dto.resp.EvaluationCreateRespDTO;
import top.xblog1.emr.services.evaluation.dto.resp.EvaluationQueryRespDTO;

/**
 *
 */

public interface EvaluationServices {
    EvaluationCreateRespDTO create(EvaluationCreateReqDTO requestParam);

    void delete(@Valid Long id);

    EvaluationQueryRespDTO queryById(@Valid Long id);

    EvaluationQueryRespDTO queryByEmrId(@Valid Long emrId);

    void update(EvaluationUpdateReqDTO requestParam);

    PageResponse<EvaluationQueryRespDTO> pageQuery(EvaluationPageQueryReqDTO requestParam);

    Boolean hasEvaluation(String emrId);
}
