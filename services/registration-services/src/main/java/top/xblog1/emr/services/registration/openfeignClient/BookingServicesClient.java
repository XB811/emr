package top.xblog1.emr.services.registration.openfeignClient;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.registration.openfeignClient.resp.BookingQueryRespDTO;

/**
 * 远程调用-预约
 */
@FeignClient("emr-booking-services")
public interface BookingServicesClient {

    /**
    * 根据医生id获取预约时间
    * @param doctorId 
    * @return Result<BookingQueryRespDTO> 
    */
    @GetMapping("/api/booking-services/v1/queryByDoctorId/{doctorId}")
    public Result<BookingQueryRespDTO> queryByDoctorId(@PathVariable("doctorId") Long doctorId);
}
