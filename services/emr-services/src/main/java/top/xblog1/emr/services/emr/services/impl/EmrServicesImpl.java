package top.xblog1.emr.services.emr.services.impl;

import cn.hutool.core.util.IdcardUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.services.emr.common.enums.EmrErrorCodeEnum;
import top.xblog1.emr.services.emr.dao.entity.EmrDO;
import top.xblog1.emr.services.emr.dao.mapper.EmrMapper;
import top.xblog1.emr.services.emr.dto.req.EmrCreateReqDTO;
import top.xblog1.emr.services.emr.dto.req.EmrUpdateReqDTO;
import top.xblog1.emr.services.emr.dto.resp.EmrCreateRespDTO;
import top.xblog1.emr.services.emr.dto.resp.EmrQueryRespDTO;
import top.xblog1.emr.services.emr.dto.resp.EmrUpdateRespDTO;
import top.xblog1.emr.services.emr.openfeignClient.DepartmentServicesClient;
import top.xblog1.emr.services.emr.openfeignClient.UserServicesClient;
import top.xblog1.emr.services.emr.openfeignClient.resp.DepartmentQueryRespDTO;
import top.xblog1.emr.services.emr.openfeignClient.resp.UserQueryActualRespDTO;
import top.xblog1.emr.services.emr.services.EmrServices;

import static top.xblog1.emr.services.emr.common.enums.EmrErrorCodeEnum.*;

/**
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmrServicesImpl implements EmrServices {

    private final UserServicesClient userServicesClient;
    private final DepartmentServicesClient departmentServicesClient;
    private final EmrMapper emrMapper;

    @Override
    public EmrCreateRespDTO create(EmrCreateReqDTO requestParam) {
        EmrDO emrDO = BeanUtil.convert(requestParam, EmrDO.class);
        //查询获取患者信息
        UserQueryActualRespDTO patientInfo = remoteCallsResultHandle(
                        userServicesClient.queryActualUserByIDAndUserType(emrDO.getPatientId(),
                        UserTypeEnum.PATIENT.code()));
        emrDO.setRealName(patientInfo.getRealName());
        emrDO.setGender(patientInfo.getGender());
        emrDO.setAge(IdcardUtil.getAgeByIdCard(patientInfo.getIdCard()));
        emrDO.setGender(patientInfo.getGender());
        //查询获取医生姓名
        UserQueryActualRespDTO doctorInfo = remoteCallsResultHandle(
                        userServicesClient.queryActualUserByIDAndUserType(emrDO.getDoctorId(),
                        UserTypeEnum.DOCTOR.code()));
        emrDO.setDoctorName(doctorInfo.getRealName());
        //查询获取科室名
        DepartmentQueryRespDTO departmentInfo = remoteCallsResultHandle(
                        departmentServicesClient.queryById(emrDO.getDepartmentId()));
        emrDO.setDepartmentName(departmentInfo.getName());
        emrDO.setDepartmentCode(departmentInfo.getCode());
        emrMapper.insert(emrDO);
        return BeanUtil.convert(emrDO, EmrCreateRespDTO.class);
    }

    @Override
    public void delete(Long id) {
        try {
            EmrDO emrDO = emrMapper.selectById(id);
            emrDO.setUpdateTime(null);
            emrMapper.updateById(emrDO);
            emrMapper.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException(DELETE_FAIL);
        }
    }

    @Override
    public EmrUpdateRespDTO update(EmrUpdateReqDTO requestParam) {
        EmrDO emrDO = BeanUtil.convert(requestParam, EmrDO.class);
        //电子病历一旦创建，患者/医生/科室信息不能再修改
        /*
        if(emrDO.getPatientId()!=null) {
            //查询获取患者信息
            UserQueryActualRespDTO patientInfo = remoteCallsResultHandle(
                    userServicesClient.queryActualUserByIDAndUserType(emrDO.getPatientId(),
                            UserTypeEnum.PATIENT.code()));
            emrDO.setRealName(patientInfo.getRealName());
            emrDO.setGender(patientInfo.getGender());
            emrDO.setAge(IdcardUtil.getAgeByIdCard(patientInfo.getIdCard()));
            emrDO.setGender(patientInfo.getGender());
        }

        //查询获取医生姓名
        if(emrDO.getDoctorId()!=null) {
            UserQueryActualRespDTO doctorInfo = remoteCallsResultHandle(
                    userServicesClient.queryActualUserByIDAndUserType(emrDO.getDoctorId(),
                            UserTypeEnum.DOCTOR.code()));
            emrDO.setDoctorName(doctorInfo.getRealName());
        }
        //查询获取科室名
        if(emrDO.getDepartmentId()!=null) {
            DepartmentQueryRespDTO departmentInfo = remoteCallsResultHandle(
                    departmentServicesClient.queryById(emrDO.getDepartmentId()));
            emrDO.setDepartmentName(departmentInfo.getName());
            emrDO.setDepartmentCode(departmentInfo.getCode());
        }
         */
        //只修改病历其他信息
        emrMapper.updateById(emrDO);
        return BeanUtil.convert(emrMapper.selectById(emrDO.getId()), EmrUpdateRespDTO.class);
    }

    @Override
    public EmrQueryRespDTO queryById(Long id) {
        EmrDO emrDO = emrMapper.selectById(id);
        if (emrDO == null) {
            throw new ServiceException(QUERY_FAIL);
        }
        return BeanUtil.convert(emrDO, EmrQueryRespDTO.class);
    }

    private <T> T remoteCallsResultHandle(Result<T> result){
        if (!result.isSuccess()) {
            throw new ServiceException(REMOTE_CALL_FAIL);
        }
        return result.getData();
    }
}
