package top.xblog1.emr.services.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.booking.dto.req.BookingCreateReqDTO;
import top.xblog1.emr.services.booking.dto.req.BookingUpdateReqDTO;
import top.xblog1.emr.services.booking.dto.resp.BookingCreateRespDTO;
import top.xblog1.emr.services.booking.dto.resp.BookingQueryRespDTO;
import top.xblog1.emr.services.booking.dto.resp.BookingUpdateRespDTO;
import top.xblog1.emr.services.booking.services.BookingServices;

/**
 *
 */
@RestController
@RequestMapping("/api/booking-services")
@RequiredArgsConstructor
public class BookingController {
    private final BookingServices bookingServices;

    /**
    * 创建预约时间
    * @param requestParam 
    * @return Result<BookingCreateRespDTO> 
    */
    @PostMapping("/v1/create")
    public Result<BookingCreateRespDTO> createBooking(@RequestBody BookingCreateReqDTO requestParam){
        return Results.success(bookingServices.create(requestParam));
    }
    /**
    * 根据id删除预约时间表
    * @param id
    * @return Result<Void>
    */
    @DeleteMapping("/v1/delete/{id}")
    public Result<Void> deleteBooking(@PathVariable Long id){
        bookingServices.delete(id);
        return Results.success();
    }
    
    /**
    * 更新预约时间表
    * @param requestParam 
    * @return Result<BookingUpdateRespDTO> 
    */
    @PutMapping("/v1/update")
    public Result<BookingUpdateRespDTO> updateBooking(@RequestBody BookingUpdateReqDTO requestParam){
        return Results.success(bookingServices.update(requestParam));
    }
    /**
    * 根据id查询预约时间表
    * @param id 
    * @return Result<BookingQueryRespDTO> 
    */
    @GetMapping("/v1/queryById/{id}")
    public Result<BookingQueryRespDTO> queryById(@PathVariable @Valid Long id){
        return Results.success(bookingServices.queryById(id));
    }

    @GetMapping("/v1/queryByDoctorId/{doctorId}")
    public Result<BookingQueryRespDTO> queryByDoctorId(@PathVariable @Valid Long doctorId){
        return Results.success(bookingServices.queryByDoctorId(doctorId));
    }
    //TODO 分页查询
}
