package top.xblog1.emr.services.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.user.dto.req.UserLoginReqDTO;
import top.xblog1.emr.services.user.dto.resp.UserLoginRespDTO;
import top.xblog1.emr.services.user.services.UserLoginService;
import top.xblog1.emr.services.user.services.UserService;

/**
 * 用户登录模块
 */
@RestController
@RequestMapping("/api/user-services")
@RequiredArgsConstructor
public class UserLoginController {
    private final UserLoginService userLoginService;

    /**
    * 用户登录
    * @param requestParam
    * @return Result<UserLoginRespDTO>
    */
    @PostMapping("/v1/login/{userType}")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam,
                                          @PathVariable @NotEmpty String userType) {
        requestParam.setUserType(userType);
        return Results.success(userLoginService.login(requestParam));
    }
    /**
     * 通过 Token 检查用户是否登录
     */
    @GetMapping("/v1/check-login/{userType}")
    public Result<Boolean> checkLogin(@RequestParam("accessToken") String accessToken
                                            , @PathVariable @NotEmpty String userType   ) {

        return Results.success(userLoginService.checkLogin(accessToken));
    }

    /**
     * 用户退出登录
     */
    @PostMapping("/v1/logout/{userType}")
    public Result<Void> logout(@RequestParam(required = false) String accessToken,
                                @PathVariable @NotEmpty String userType) {
        userLoginService.logout(accessToken,userType);
        return Results.success();
    }

    /**
    * 获取短信验证码
    * @param phone 
     * @param userType 
    * @return Result<Void> 
    */
    @GetMapping("/v1/getVerifyCode/{userType}")
    public Result<Void> getVerifyCode(@Valid String phone,
                                        @PathVariable @NotEmpty String userType) {
        userLoginService.getVerifyCode(phone,userType);
        return Results.success();
    }
}
