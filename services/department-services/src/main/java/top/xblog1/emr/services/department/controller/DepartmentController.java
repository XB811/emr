package top.xblog1.emr.services.department.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.department.dto.req.DepartmentInsertReqDTO;
import top.xblog1.emr.services.department.dto.req.DepartmentUpdateReqDTO;
import top.xblog1.emr.services.department.dto.resp.DepartmentQueryRespDTO;
import top.xblog1.emr.services.department.dto.resp.DepartmentUpdateRespDTO;
import top.xblog1.emr.services.department.services.DepartmentServices;

import java.util.List;

/**
 *
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
    @PostMapping("/create")
    public Result<Long> createDepartment(@RequestBody @Valid DepartmentInsertReqDTO requestParam){

        return Results.success(departmentServices.create(requestParam));
    }
    
    /**
    * 删除科室
    * @param id 
    * @return Result<Void> 
    */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteDepartment(@PathVariable Long id){
        departmentServices.delete(id);
        return Results.success();
    }
    
    /**
    * 更新科室信息
    * @param requestParam 
    * @return Result<DepartmentUpdateRespDTO> 
    */
    @PutMapping("/update")
    public Result<DepartmentUpdateRespDTO> updateDepartment(@RequestBody @Valid DepartmentUpdateReqDTO requestParam){
        return Results.success(departmentServices.update(requestParam));
    }
    
    /**
    * 根据id查询科室
    * @param id 
    * @return Result<DepartmentQueryRespDTO> 
    */
    @GetMapping("/queryById/{id}")
    public Result<DepartmentQueryRespDTO> queryById(@PathVariable @Valid Long id){
        return Results.success(departmentServices.queryById(id));
    }
    /**
    * 查询全部科室
    * @return Result<List<DepartmentQueryRespDTO>>
    */
    @GetMapping("/queryAll")
    public Result<List<DepartmentQueryRespDTO>> queryAll(){
        return Results.success(departmentServices.queryAll());
    }
    //TODO 分页查询
}
