package top.xblog1.emr.services.emr.openfeignClient;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.services.emr.openfeignClient.resp.UserQueryActualRespDTO;

/**
 *
 */
@FeignClient("emr-user-services")
public interface UserServicesClient {

    @GetMapping("/api/user-services/v1/actual/query")
    Result<UserQueryActualRespDTO> queryActualUserByIDAndUserType(@RequestParam("id")  Long id,
                                                                  @RequestParam("userType")  String userType);
}
