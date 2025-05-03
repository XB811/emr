package top.xblog1.emr.services.department.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.department.dto.req.DepartmentInsertReqDTO;
import top.xblog1.emr.services.department.dto.req.DepartmentPageQueryReqDTO;
import top.xblog1.emr.services.department.dto.req.DepartmentUpdateReqDTO;
import top.xblog1.emr.services.department.dto.resp.DepartmentQueryRespDTO;
import top.xblog1.emr.services.department.dto.resp.DepartmentUpdateRespDTO;
import top.xblog1.emr.services.department.services.DepartmentServices;

import java.util.List;

/**
 * 部门管理
 */
@RestController
@RequestMapping("/api/department-services")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentServices departmentServices;
    /**
    * 新增科室
    * @param requestParam 
    * @return Result<Long> 
    */
    @PostMapping("/v1/create")
    public Result<Long> createDepartment(@RequestBody @Valid DepartmentInsertReqDTO requestParam){

        return Results.success(departmentServices.create(requestParam));
    }
    
    /**
    * 删除科室
    * @param id 
    * @return Result<Void> 
    */
    @DeleteMapping("/v1/delete/{id}")
    public Result<Void> deleteDepartment(@PathVariable Long id){
        departmentServices.delete(id);
        return Results.success();
    }
    
    /**
    * 更新科室信息
    * @param requestParam 
    * @return Result<DepartmentUpdateRespDTO> 
    */
    @PutMapping("/v1/update")
    public Result<DepartmentUpdateRespDTO> updateDepartment(@RequestBody @Valid DepartmentUpdateReqDTO requestParam){
        return Results.success(departmentServices.update(requestParam));
    }
    
    /**
    * 根据id查询科室
    * @param id 
    * @return Result<DepartmentQueryRespDTO> 
    */
    @GetMapping("/v1/queryById/{id}")
    public Result<DepartmentQueryRespDTO> queryById(@PathVariable @Valid Long id){
        return Results.success(departmentServices.queryById(id));
    }
    /**
    * 查询全部科室
    * @return Result<List<DepartmentQueryRespDTO>>
    */
    @GetMapping("/v1/queryAll")
    public Result<List<DepartmentQueryRespDTO>> queryAll(){
        return Results.success(departmentServices.queryAll());
    }
    // 分页查询
    /**
    * 分页查询
    * @param requestParam 
    * @return Result<PageResponse<DepartmentQueryRespDTO>> 
    */
    @GetMapping("/v1/pageQuery")
    public Result<PageResponse<DepartmentQueryRespDTO>> pageQuery(@RequestBody DepartmentPageQueryReqDTO requestParam){
        return  Results.success(departmentServices.pageQuery(requestParam));
    }
}
