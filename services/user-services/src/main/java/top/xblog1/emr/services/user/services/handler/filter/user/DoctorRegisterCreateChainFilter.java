package top.xblog1.emr.services.user.services.handler.filter.user;

import cn.hutool.core.lang.Validator;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;

import java.util.Objects;

/**
 * 医生参数校验
 */

public class DoctorRegisterCreateChainFilter implements UserRegisterCreateChainFilter<UserRegisterReqDTO>{
    @Override
    public void handler(UserRegisterReqDTO requestParam) {
        //如果用户类型为医生
        if(Objects.equals(requestParam.getUserType(), UserTypeEnum.DOCTOR.code())){
            if(Objects.isNull(requestParam.getGender()))
                throw new ClientException(UserRegisterErrorCodeEnum.GENDER_NOTNULL);
            else if(Objects.isNull(requestParam.getDepartmentId()))
                throw new ClientException(UserRegisterErrorCodeEnum.DEPARTMENT_ID_NOTNULL);
            else if(requestParam.getTitle().isEmpty())
                throw new ClientException(UserRegisterErrorCodeEnum.TITLE_NOTNULL);
            else if(requestParam.getSpecialty().isEmpty())
                throw new ClientException(UserRegisterErrorCodeEnum.SPECIALTY_NOTNULL);
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
