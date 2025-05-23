package top.xblog1.emr.services.booking.services.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.database.toolkit.PageUtil;
import top.xblog1.emr.services.booking.dao.entity.BookingDO;
import top.xblog1.emr.services.booking.dao.mapper.BookingMapper;
import top.xblog1.emr.services.booking.dto.req.BookingCreateReqDTO;
import top.xblog1.emr.services.booking.dto.req.BookingPageQueryReqDTO;
import top.xblog1.emr.services.booking.dto.req.BookingUpdateReqDTO;
import top.xblog1.emr.services.booking.dto.resp.BookingCreateRespDTO;
import top.xblog1.emr.services.booking.dto.resp.BookingQueryRespDTO;
import top.xblog1.emr.services.booking.dto.resp.BookingUpdateRespDTO;
import top.xblog1.emr.services.booking.openfeignClient.UserServicesClient;
import top.xblog1.emr.services.booking.openfeignClient.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.booking.services.BookingServices;

import static top.xblog1.emr.services.booking.common.enums.BookingErrorCodeEnum.*;

/**
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServicesImpl implements BookingServices {
    private final BookingMapper bookingMapper;
    private final UserServicesClient userServicesClient;
    @Override
    public BookingCreateRespDTO create(BookingCreateReqDTO requestParam) {
        if(requestParam.getDoctorId()==null)
            throw new ClientException(DOCTOR_ID_NOTNULL);
        BookingDO bookingDO = BeanUtil.convert(requestParam, BookingDO.class);
        //openfeign获取医生姓名
        UserQueryActualRespDTO doctorInfo = remoteCallsResultHandle(
                        userServicesClient.queryActualUserByIDAndUserType(
                                requestParam.getDoctorId(),
                                UserTypeEnum.DOCTOR.code()));
        bookingDO.setDoctorName(doctorInfo.getRealName());
        try {
            bookingMapper.insert(bookingDO);
        } catch (DuplicateKeyException e) {
            throw new ClientException("该医生id已经创建预约");
        }
        return BeanUtil.convert(bookingDO, BookingCreateRespDTO.class);
    }

    @Override
    public void delete(Long id) {
        if(id==null)throw new ClientException(ID_NOTNULL);
        BookingDO bookingDO = bookingMapper.selectById(id);
        if(bookingDO==null)throw new ClientException(ID_NOTNULL);

        bookingDO.setUpdateTime(null);
        bookingMapper.updateById(bookingDO);
        bookingMapper.deleteById(id);
    }

    @Override
    public BookingUpdateRespDTO update(BookingUpdateReqDTO requestParam) {
        if(requestParam.getDoctorId()==null)
            throw new ClientException(DOCTOR_ID_NOTNULL);
        BookingDO bookingDO = BeanUtil.convert(requestParam, BookingDO.class);
        //openfeign获取医生姓名
        UserQueryActualRespDTO doctorInfo = remoteCallsResultHandle(
                userServicesClient.queryActualUserByIDAndUserType(
                        requestParam.getDoctorId(),
                        UserTypeEnum.DOCTOR.code()));
        bookingDO.setDoctorName(doctorInfo.getRealName());
        bookingMapper.updateById(bookingDO);
        log.info(bookingDO.toString());
        return BeanUtil.convert(bookingMapper.selectById(bookingDO.getId()), BookingUpdateRespDTO.class);
    }

    @Override
    public BookingQueryRespDTO queryById(Long id) {
        if(id==null)throw new ClientException(ID_NOTNULL);
        BookingDO bookingDO = bookingMapper.selectById(id);
        if(bookingDO==null)throw new ServiceException(BOOKING_NOT_FOUNd);
        return BeanUtil.convert(bookingDO, BookingQueryRespDTO.class);

    }

    @Override
    public BookingQueryRespDTO queryByDoctorId(Long doctorId) {
        if(doctorId==null)throw new ClientException(DOCTOR_ID_NOTNULL);
        LambdaQueryWrapper<BookingDO> queryWrapper = Wrappers.lambdaQuery(BookingDO.class)
                .eq(BookingDO::getDoctorId,doctorId);
        BookingDO bookingDO = bookingMapper.selectOne(queryWrapper);
        if(bookingDO==null)throw new ServiceException(BOOKING_NOT_FOUNd);
        return BeanUtil.convert(bookingDO, BookingQueryRespDTO.class);
    }

    @Override
    public PageResponse<BookingQueryRespDTO> pageQuery(BookingPageQueryReqDTO requestParam) {
        LambdaQueryWrapper<BookingDO> queryWrapper = Wrappers.lambdaQuery(BookingDO.class);
        if(requestParam.getDoctorId()!=null)
            queryWrapper.like(BookingDO::getDoctorId,requestParam.getDoctorId());
        if(requestParam.getDoctorName()!=null&& !requestParam.getDoctorName().isEmpty())
            queryWrapper.like(BookingDO::getDoctorName,requestParam.getDoctorName());
        if(requestParam.getIsAvailable()!=null)
            queryWrapper.eq(BookingDO::getIsAvailable,requestParam.getIsAvailable());
        queryWrapper.orderByDesc(BookingDO::getUpdateTime);
        IPage<BookingDO> bookingDOIPageDO = bookingMapper.selectPage(PageUtil.convert(requestParam),queryWrapper);
        return PageUtil.convert(bookingDOIPageDO, each -> {

            return BeanUtil.convert(each, BookingQueryRespDTO.class);
        });
    }

    private <T> T remoteCallsResultHandle(Result<T> result){
        if (!result.isSuccess()) {
            throw new ServiceException(REMOTE_CALL_FAIL);
        }
        return result.getData();
    }
}
