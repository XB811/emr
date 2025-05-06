package top.xblog1.emr.services.evaluation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.evaluation.dto.req.EvaluationCreateReqDTO;
import top.xblog1.emr.services.evaluation.dto.req.EvaluationPageQueryReqDTO;
import top.xblog1.emr.services.evaluation.dto.req.EvaluationUpdateReqDTO;
import top.xblog1.emr.services.evaluation.dto.resp.EvaluationCreateRespDTO;
import top.xblog1.emr.services.evaluation.dto.resp.EvaluationQueryRespDTO;
import top.xblog1.emr.services.evaluation.services.EvaluationServices;

/**
 * 就诊评价管理
 */
@RestController
@RequestMapping("/api/evaluation-services")
@RequiredArgsConstructor
public class EvaluationController {
    private final EvaluationServices evaluationServices;
    /**
    * 新增评价
    * @param requestParam 
    * @return Result<EvaluationCreateRespDTO> 
    */
    @PostMapping("/v1/create")
    public Result<EvaluationCreateRespDTO> createEvaluation(@RequestBody EvaluationCreateReqDTO requestParam) {
        return Results.success(evaluationServices.create(requestParam));
    }
    
    /**
    * 删除评价
    * @param id 
    * @return Result<Void> 
    */
    @DeleteMapping("/v1/delete/{id}")
    public Result<Void> deleteEvaluation(@PathVariable @Valid Long id){
        evaluationServices.delete(id);
        return Results.success();
    }
    
    /**
    * 根据id查询评价
    * @param id 
    * @return Result<EvaluationQueryRespDTO> 
    */
    @GetMapping("/v1/queryById/{id}")
    public Result<EvaluationQueryRespDTO> queryById(@PathVariable @Valid Long id){
        return Results.success(evaluationServices.queryById(id));
    }
    /**
    * 根据emrid修改评价
    * @param emrId 
    * @return Result<EvaluationQueryRespDTO> 
    */
    @GetMapping("/v1/queryByEmrId/{emrId}")
    public Result<EvaluationQueryRespDTO> queryByEmrId(@PathVariable @Valid Long emrId){
        return Results.success(evaluationServices.queryByEmrId(emrId));
    }
    
    /**
    * 更新评价
    * @param requestParam 
    * @return Result<Void> 
    */
    @PutMapping("/v1/update")
    public Result<Void> updateEmr(@RequestBody EvaluationUpdateReqDTO requestParam){
        evaluationServices.update(requestParam);
        return Results.success();
    }
    /**
    * 分页查询
    * @param requestParam 
    * @return Result<PageResponse<EvaluationQueryRespDTO>> 
    */
    @GetMapping("/v1/pageQuery")
    public Result<PageResponse<EvaluationQueryRespDTO>> pageQuery( EvaluationPageQueryReqDTO requestParam){
        return Results.success(evaluationServices.pageQuery(requestParam));
    }
    /**
    * 查询emr是否已经被评价
    * @param emrId 
    * @return Result<Boolean> 
    */
    @GetMapping("/v1/hasEvaluation/{emrId}")
    public Result<Boolean> hasEvaluation(@PathVariable String emrId){
        return Results.success(evaluationServices.hasEvaluation(emrId));
    }

    /**
    * 查询医生的平均分
    * @param doctorId 
    * @return Result<Double> 
    */
    @GetMapping("/v1/getAverageRating/{doctorId}")
    public Result<Double> getAverageRating(@PathVariable String doctorId){
        return Results.success(evaluationServices.getAverageRating(doctorId));
    }
}
