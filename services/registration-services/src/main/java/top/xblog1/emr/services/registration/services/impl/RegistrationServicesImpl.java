package top.xblog1.emr.services.registration.services.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.database.toolkit.PageUtil;
import top.xblog1.emr.services.registration.dao.entity.RegistrationDO;
import top.xblog1.emr.services.registration.dao.mapper.RegistrationMapper;
import top.xblog1.emr.services.registration.dto.req.RegistrationCreateReqDTO;
import top.xblog1.emr.services.registration.dto.req.RegistrationPageQueryReqDTO;
import top.xblog1.emr.services.registration.dto.req.RegistrationUpdateReqDTO;
import top.xblog1.emr.services.registration.dto.resp.RegistrationCreateRespDTO;
import top.xblog1.emr.services.registration.dto.resp.RegistrationQueryRespDTO;
import top.xblog1.emr.services.registration.dto.resp.RegistrationUpdateRespDTO;
import top.xblog1.emr.services.registration.openfeignClient.BookingServicesClient;
import top.xblog1.emr.services.registration.openfeignClient.resp.BookingQueryRespDTO;
import top.xblog1.emr.services.registration.services.RegistrationServices;

import static top.xblog1.emr.services.registration.common.enums.IsFinishEnum.FINISHED;
import static top.xblog1.emr.services.registration.common.enums.IsFinishEnum.NOT_FINISH;
import static top.xblog1.emr.services.registration.common.enums.RegistrationErrorCodeEnum.*;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class RegistrationServicesImpl implements RegistrationServices {

    private final RegistrationMapper registrationMapper;
    private final BookingServicesClient bookingServicesClient;
    private final Mapper mapper;

    @Override
    public RegistrationCreateRespDTO create(RegistrationCreateReqDTO requestParam) {
        if(requestParam.getPatientId()==null)
            throw new ClientException("患者ID不能为空");
        else if(requestParam.getDoctorId()==null)
            throw new ClientException("医生ID不能为空");
        else if(requestParam.getAppointmentDate()==null)
            throw new ClientException("预约日期不能为空");
        else if(requestParam.getAppointmentTime()==null)
            throw new ClientException("预约时间不能为空");
        else if(requestParam.getAppointmentDate().before(new DateTime()))
            throw new ClientException("预约时间错误");
        // 创建挂号应当先判断当前患者是否有未完成的挂号
        LambdaQueryWrapper<RegistrationDO> queryWrapper = Wrappers.lambdaQuery(RegistrationDO.class)
                .eq(RegistrationDO::getPatientId, requestParam.getPatientId())
                .eq(RegistrationDO::getIsFinish, NOT_FINISH.code());
        //如果当前患者存在未完成的预约
        if (registrationMapper.selectCount(queryWrapper)>0) {
            throw new ClientException(PATIENT_HAVE_NOT_FINISH_REGISTERED);
        }
        RegistrationDO registrationDO = BeanUtil.convert(requestParam, RegistrationDO.class);

        //远程调用获取该医生的预约时间管理
        BookingQueryRespDTO bookingInfo = remoteCallsResultHandle(bookingServicesClient.queryByDoctorId(registrationDO.getDoctorId()));
        if(!bookingInfo.getIsAvailable()){
            throw new ServiceException("该医生当前不可预约");
        }
        String binaryString = Long.toBinaryString(bookingInfo.getAvailableTime());
        String availableTime = String.format("%14s", binaryString);
        int day = DateUtil.dayOfWeek(requestParam.getAppointmentDate());
        //将时间由 日123456转为 123456日
        day--;
        if(day==0)day=7;
        int index=(day - 1) * 2 + requestParam.getAppointmentTime();
        char isAvailable = availableTime.charAt(index);
        if (isAvailable == '0') {
            throw new ServiceException("该时间段不可预约");
        }
        try {
            registrationDO.setIsFinish(NOT_FINISH.code());
            registrationMapper.insert(registrationDO);
        } catch (Exception e) {
            throw new ServiceException("预约失败");
        }

        return BeanUtil.convert(registrationDO,RegistrationCreateRespDTO.class);
    }

    @Override
    public void delete(Long id) {
        if(id==null)
            throw new ClientException(ID_NOTNULL);
        RegistrationDO registrationDO = registrationMapper.selectById(id);
        registrationDO.setUpdateTime(null);
        registrationMapper.updateById(registrationDO);
        registrationMapper.deleteById(id);
    }

    @Override
    public RegistrationUpdateRespDTO update(RegistrationUpdateReqDTO requestParam) {
        if(requestParam.getPatientId()==null)
            throw new ClientException("患者ID不能为空");
        else if(requestParam.getDoctorId()==null)
            throw new ClientException("医生ID不能为空");
        else if(requestParam.getAppointmentDate()==null)
            throw new ClientException("预约日期不能为空");
        else if(requestParam.getAppointmentTime()==null)
            throw new ClientException("预约时间不能为空");
        else if(requestParam.getAppointmentDate().before(new DateTime()))
            throw new ClientException("预约时间错误");
        RegistrationDO registrationDO = BeanUtil.convert(requestParam, RegistrationDO.class);
        //远程调用获取该医生的预约时间管理
        BookingQueryRespDTO bookingInfo = remoteCallsResultHandle(bookingServicesClient.queryByDoctorId(registrationDO.getDoctorId()));
        if(!bookingInfo.getIsAvailable()){
            throw new ServiceException("该医生当前不可预约");
        }
        String binaryString = Long.toBinaryString(bookingInfo.getAvailableTime());
        String availableTime = String.format("%014s", binaryString).replace(' ', '0');
        int day = DateUtil.dayOfWeek(requestParam.getAppointmentDate());
        //将时间由 日123456转为 123456日
        day--;
        if(day==0)day=7;
        int index=(day - 1) * 2 + requestParam.getAppointmentTime();
        char isAvailable = availableTime.charAt(index);
        if (isAvailable == '0') {
            throw new ServiceException("该时间段不可预约");
        }
        try {
            registrationMapper.updateById(registrationDO);
        } catch (Exception e) {
            throw new ServiceException("预约失败");
        }
        return BeanUtil.convert(registrationDO,RegistrationUpdateRespDTO.class);
    }

    @Override
    public RegistrationQueryRespDTO queryById(Long id) {
        if(id==null)
            throw new ClientException(ID_NOTNULL);
        return BeanUtil.convert(registrationMapper.selectById(id),RegistrationQueryRespDTO.class);
    }

    @Override
    public void finish(Long id) {
        if(id==null)
            throw new ClientException(ID_NOTNULL);
        LambdaUpdateWrapper<RegistrationDO> updateWrapper = Wrappers.lambdaUpdate(RegistrationDO.class)
                .eq(RegistrationDO::getId, id)
                .set(RegistrationDO::getIsFinish, FINISHED.code());
        try {
            RegistrationDO registrationDO;
            try {
                 registrationDO= registrationMapper.selectById(id);
            } catch (Exception e) {
                throw new ServiceException("挂号id不存在");
            }
            registrationMapper.update(registrationDO ,updateWrapper);
        }catch (Exception e) {
            throw new ServiceException("挂号完成失败");
        }
    }

    @Override
    public PageResponse<RegistrationQueryRespDTO> pageQuery(RegistrationPageQueryReqDTO requestParam) {
        LambdaQueryWrapper<RegistrationDO> queryWrapper = Wrappers.lambdaQuery(RegistrationDO.class);
        if(requestParam.getPatientId() !=null)
            queryWrapper.eq(RegistrationDO::getPatientId, requestParam.getPatientId());
        if(requestParam.getDoctorId()!=null)
            queryWrapper.eq(RegistrationDO::getDoctorId,requestParam.getDoctorId());
        if(requestParam.getAppointmentDate()!=null)
            queryWrapper.eq(RegistrationDO::getAppointmentDate,requestParam.getAppointmentDate());
        if(requestParam.getIsFinish()!=null)
            queryWrapper.eq(RegistrationDO::getIsFinish,requestParam.getIsFinish());
        if(requestParam.getAppointmentTime()!=null)
            queryWrapper.eq(RegistrationDO::getAppointmentTime,requestParam.getAppointmentTime());
        queryWrapper.orderByDesc(RegistrationDO::getUpdateTime);
        IPage<RegistrationDO> registrationDOIPage =registrationMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        return PageUtil.convert(registrationDOIPage, each -> {

            return BeanUtil.convert(each, RegistrationQueryRespDTO.class);
        });

    }

    private <T> T remoteCallsResultHandle(Result<T> result){
        if (!result.isSuccess()) {
            throw new ServiceException(REMOTE_CALL_FAIL);
        }
        return result.getData();
    }

}
