package top.xblog1.emr.services.emr.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.emr.dto.req.EmrCreateReqDTO;
import top.xblog1.emr.services.emr.dto.req.EmrPageQueryReqDTO;
import top.xblog1.emr.services.emr.dto.req.EmrUpdateReqDTO;
import top.xblog1.emr.services.emr.dto.resp.EmrCreateRespDTO;
import top.xblog1.emr.services.emr.dto.resp.EmrQueryRespDTO;
import top.xblog1.emr.services.emr.dto.resp.EmrUpdateRespDTO;
import top.xblog1.emr.services.emr.services.EmrServices;

/**
 * 病历管理
 */
@RestController
@RequestMapping("/api/emr-services")
@RequiredArgsConstructor
public class EmrController {
    private final EmrServices emrServices;

    /**
    * 创建病历
    * @param requestParam
    * @return Result<EmrCreateRespDTO>
    */
    @PostMapping("/v1/create")
    public Result<EmrCreateRespDTO> createEmr(@RequestBody EmrCreateReqDTO requestParam) {
        return Results.success(emrServices.create(requestParam));
    }
    /**
    * 删除病历
    * @param id
    * @return Result<Void>
    */
    @DeleteMapping("/v1/delete/{id}")
    public Result<Void> deleteEmr(@PathVariable Long id){
        emrServices.delete(id);
        return Results.success();
    }
    /**
    * 更新病历
    * @param requestParam
    * @return Result<EmrUpdateRespDTO>
    */
    @PutMapping("/v1/update")
    public Result<EmrUpdateRespDTO> updateEmr(@RequestBody EmrUpdateReqDTO requestParam){
        return Results.success(emrServices.update(requestParam));
    }
    /**
    * 根据id查询病历
    * @param id
    * @return Result<EmrQueryRespDTO>
    */
    @GetMapping("/v1/queryById/{id}")
    public Result<EmrQueryRespDTO> queryById(@PathVariable Long id){
        return Results.success(emrServices.queryById(id));
    }
    /**
    * 分页查询
    * @param requestParam
    * @return
    */
    @GetMapping("/v1/pageQuery")
    public Result<PageResponse<EmrQueryRespDTO>> pageQuery( EmrPageQueryReqDTO requestParam){
        return Results.success(emrServices.pageQuery(requestParam));
    }
}
