package top.xblog1.emr.services.registration.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.registration.dto.req.RegistrationCreateReqDTO;
import top.xblog1.emr.services.registration.dto.req.RegistrationUpdateReqDTO;
import top.xblog1.emr.services.registration.dto.resp.RegistrationCreateRespDTO;
import top.xblog1.emr.services.registration.dto.resp.RegistrationQueryRespDTO;
import top.xblog1.emr.services.registration.dto.resp.RegistrationUpdateRespDTO;
import top.xblog1.emr.services.registration.services.RegistrationServices;

/**
 * 挂号管理
 */
@RestController
@RequestMapping("/api/registration-services")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationServices registrationServices;
    /**
    * 创建挂号
    * @param requestParam 
    * @return Result<RegistrationCreateRespDTO> 
    */
    @PostMapping("/v1/create")
    public Result<RegistrationCreateRespDTO> createRegistration(@RequestBody @Valid RegistrationCreateReqDTO requestParam) {
        return Results.success(registrationServices.create(requestParam));
    }

    /**
    * 删除挂号
    * @param id 
    * @return Result<Void> 
    */
    @DeleteMapping("/v1/delete/{id}")
    public Result<Void> deleteRegistration(@PathVariable @Valid Long id){
        registrationServices.delete(id);
        return Results.success();
    }

    /**
    * 更新挂号
    * @param requestParam 
    * @return Result<RegistrationUpdateRespDTO> 
    */
    @PutMapping("/v1/update")
    public Result<RegistrationUpdateRespDTO> updateRegistration(@RequestBody RegistrationUpdateReqDTO requestParam){
        return Results.success(registrationServices.update(requestParam));
    }

    /**
    * 根据id查询挂号
    * @param id 
    * @return Result<RegistrationQueryRespDTO> 
    */
    @GetMapping("/v1/queryById/{id}")
    public Result<RegistrationQueryRespDTO> queryById(@PathVariable @Valid Long id){
        return Results.success(registrationServices.queryById(id));
    }
    
    /**
    * 完成挂号
    * @param id 
    * @return Result<Void> 
    */
    @PutMapping("/v1/finish/{id}")
    public Result<Void> finish(@PathVariable @Valid Long id){
        registrationServices.finish(id);
        return Results.success();
    }
    //TODO 分页查询
}
