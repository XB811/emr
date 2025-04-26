package top.xblog1.emr.services.emr.openfeignClient;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.emr.openfeignClient.resp.DepartmentQueryRespDTO;

/**
 *
 */
@FeignClient("emr-department-services")
public interface DepartmentServicesClient {
    @GetMapping("/api/department-services/v1/queryById/{id}")
     Result<DepartmentQueryRespDTO> queryById(@PathVariable("id")  Long id);
}
