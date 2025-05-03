package top.xblog1.emr.services.emr.openfeignClient;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.services.emr.openfeignClient.resp.UserQueryActualRespDTO;

/**
 * 远程调用-user
 */
@FeignClient("emr-user-services")
public interface UserServicesClient {

    /**
    * 获取用户真实信息
    * @param id 
     * @param userType 
    * @return Result<UserQueryActualRespDTO> 
    */
    @GetMapping("/api/user-services/v1/actualQuery/{userType}/{id}")
    Result<UserQueryActualRespDTO> queryActualUserByIDAndUserType(@PathVariable("id")  Long id,
                                                                  @PathVariable("userType") String userType);
}
