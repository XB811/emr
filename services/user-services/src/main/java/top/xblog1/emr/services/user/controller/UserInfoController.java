package top.xblog1.emr.services.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.user.core.UserInfoDTO;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.user.dto.req.UserDeletionReqDTO;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;
import top.xblog1.emr.services.user.dto.req.UserUpdateReqDTO;
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
public class UserInfoController {
    private final UserService userService;
    private final UserLoginService userLoginService;

    /**
    * 根据用户id和用户类型查询用户信息
    * @param id 
     * @param userType 
    * @return Result<UserQueryRespDTO> 
    */
    @GetMapping("/v1/query")
    public Result<UserQueryRespDTO> queryUserByIDAndUserType(@RequestParam("id") @NotEmpty Long id,
                                                             @RequestParam("userType") @NotEmpty String userType){
        return Results.success(userService.queryUserByIDAndUserType(id,userType));
    }

    /**
    * 根据用户id和用户类型查询用户无脱敏信息
    * @param id
     * @param userType
    * @return Result<UserQueryActualRespDTO>
    */
    @GetMapping("/v1/actual/query")
    public Result<UserQueryActualRespDTO> queryActualUserByIDAndUserType(@RequestParam("id") @NotEmpty Long id,
                                                                         @RequestParam("userType") @NotEmpty String userType){
        return Results.success(userService.queryActualUserByIDAndUserType(id,userType));
    }
    /**
     * 检查用户名是否已存在
     */
    @GetMapping("/v1/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") @NotEmpty String username ,
                                       @RequestParam("userType") @NotEmpty String userType) {
        return Results.success(userLoginService.hasUsername(username,userType));
    }

    /**
    * 用户注册
    * @param requestParam
    * @return Result<UserRegisterRespDTO>
    */
    @PostMapping("/v1/register")
    public Result<UserRegisterRespDTO> register(@RequestBody @Valid UserRegisterReqDTO requestParam) {
        return Results.success(userLoginService.register(requestParam));
    }

    /**
     * 根据id修改用户
     */
    @PostMapping("/v1/update")
    public Result<Void> update(@RequestBody @Valid UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }
    /**
     * 注销用户
     */
    @PostMapping("/v1/deletion")
    public Result<Void> deletion(@RequestBody @Valid UserDeletionReqDTO requestParam) {
        userLoginService.deletion(requestParam);
        return Results.success();
    }
    // TODO 根据多种条件分页查询用户
}
