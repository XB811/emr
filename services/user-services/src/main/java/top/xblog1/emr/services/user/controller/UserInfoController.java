package top.xblog1.emr.services.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.user.core.UserInfoDTO;
import top.xblog1.emr.framework.starter.user.toolkit.JWTUtil;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.user.dto.req.UpdatePasswordReqDTO;
import top.xblog1.emr.services.user.dto.req.UserDeletionReqDTO;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;
import top.xblog1.emr.services.user.dto.req.UserUpdateReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserInfoQueryByTokenRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserQueryRespDTO;
import top.xblog1.emr.services.user.dto.resp.UserRegisterRespDTO;
import top.xblog1.emr.services.user.services.UserLoginService;
import top.xblog1.emr.services.user.services.UserService;

/**
 * 用户信息管理模块
 */
@RestController
@RequestMapping("/api/user-services")
@RequiredArgsConstructor
@Slf4j
public class UserInfoController {
    private final UserService userService;
    private final UserLoginService userLoginService;
    // TODO 所有接口 dto/req 添加注解参数校验
    // TODO 数据库phone字段和idCard字段加密
    // TODO 给 dao/entity 添加注解，控制更新或插入策略

    /**
    * 根据用户id和用户类型查询用户信息
    * @param id 
     * @param userType 
    * @return Result<UserQueryRespDTO> 
    */
    @GetMapping("/v1/query/{userType}/{id}")
    public Result<UserQueryRespDTO> queryUserByIDAndUserType(@PathVariable @NotEmpty Long id,
                                                             @PathVariable @NotEmpty String userType){
        UserQueryRespDTO userQueryRespDTO = userService.queryUserByIDAndUserType(id, userType);
        //log.info(String.valueOf(userQueryRespDTO));
        return Results.success(userQueryRespDTO);
    }

    /**
    * 根据用户id和用户类型查询用户无脱敏信息
    * @param id
     * @param userType
    * @return Result<UserQueryActualRespDTO>
    */
    @GetMapping("/v1/actualQuery/{userType}/{id}")
    public Result<UserQueryActualRespDTO> queryActualUserByIDAndUserType(@PathVariable @NotEmpty Long id,
                                                                         @PathVariable @NotEmpty String userType){
        return Results.success(userService.queryActualUserByIDAndUserType(id,userType));
    }
    /**
     * 检查用户名是否已存在
     */
    @GetMapping("/v1/has-username/{userType}/{username}")
    public Result<Boolean> hasUsername(@PathVariable @NotEmpty String username ,
                                       @PathVariable @NotEmpty String userType) {
        return Results.success(userLoginService.hasUsername(username,userType));
    }

    /**
    * 用户注册
    * @param requestParam
    * @return Result<UserRegisterRespDTO>
    */
    @PostMapping("/v1/register/{userType}")
    public Result<UserRegisterRespDTO> register(@RequestBody @Valid UserRegisterReqDTO requestParam ,
                                                @PathVariable @NotEmpty String userType) {
        requestParam.setUserType(userType);
        return Results.success(userLoginService.register(requestParam));
    }

    /**
     * 根据id修改用户
     */
    @PutMapping("/v1/update/{userType}")
    public Result<Void> update(@RequestBody @Valid UserUpdateReqDTO requestParam,
                               @PathVariable @NotEmpty String userType) {
        requestParam.setUserType(userType);
        userService.update(requestParam);
        return Results.success();
    }
    /**
     * 注销用户
     */
    @DeleteMapping("/v1/deletion/{userType}")
    public Result<Void> deletion(@RequestParam @Valid String username,
                                 @PathVariable @NotEmpty String userType) {
        UserDeletionReqDTO requestParam=UserDeletionReqDTO.builder()
                                        .username(username)
                                        .userType(userType)
                                        .build();
        userLoginService.deletion(requestParam);
        return Results.success();
    }
    /**
    * 使用token获取用户信息
    * @param token 
    * @return Result<UserInfoQueryByTokenRespDTO> 
    */
    @GetMapping("/v1/getUserInfoByToken")
    public Result<UserInfoQueryByTokenRespDTO> getUserInfoByToken(@RequestParam @NotEmpty String token) {
        return Results.success(userLoginService.getUserInfoByToken(token));
    }
    // TODO 根据多种条件分页查询用户

    @PutMapping("/v1/updatePassword/{userType}")
    public Result<Void> updatePassword(@RequestBody @Valid UpdatePasswordReqDTO requestParam,
                                       @PathVariable @NotEmpty String userType) {
        userService.updatePassword(requestParam,userType);
        return Results.success();
    }
}
