package top.xblog1.emr.services.booking.openfeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.services.booking.openfeignClient.resp.UserQueryActualRespDTO;

/**
 *
 */
@FeignClient("emr-user-services")
public interface UserServicesClient {

    @GetMapping("/api/user-services/v1/actualQuery/{userType}/{id}")
    Result<UserQueryActualRespDTO> queryActualUserByIDAndUserType(@PathVariable("id")  Long id,
                                                                  @PathVariable("userType") String userType);
}
