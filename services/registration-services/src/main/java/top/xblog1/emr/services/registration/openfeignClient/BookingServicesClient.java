package top.xblog1.emr.services.registration.openfeignClient;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.registration.openfeignClient.resp.BookingQueryRespDTO;

/**
 *
 */
@FeignClient("emr-booking-services")
public interface BookingServicesClient {

    @GetMapping("/api/booking-services/v1/queryByDoctorId/{doctorId}")
    public Result<BookingQueryRespDTO> queryByDoctorId(@PathVariable("doctorId") Long doctorId);
}
