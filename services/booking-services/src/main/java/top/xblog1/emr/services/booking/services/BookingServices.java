package top.xblog1.emr.services.booking.services;

import jakarta.validation.Valid;
import top.xblog1.emr.services.booking.dto.req.BookingCreateReqDTO;
import top.xblog1.emr.services.booking.dto.req.BookingUpdateReqDTO;
import top.xblog1.emr.services.booking.dto.resp.BookingCreateRespDTO;
import top.xblog1.emr.services.booking.dto.resp.BookingQueryRespDTO;
import top.xblog1.emr.services.booking.dto.resp.BookingUpdateRespDTO;

/**
 *
 */

public interface BookingServices {
    BookingCreateRespDTO create(BookingCreateReqDTO requestParam);

    void delete(Long id);

    BookingUpdateRespDTO update(BookingUpdateReqDTO requestParam);

    BookingQueryRespDTO queryById(Long id);

    BookingQueryRespDTO queryByDoctorId(@Valid Long doctorId);
}
