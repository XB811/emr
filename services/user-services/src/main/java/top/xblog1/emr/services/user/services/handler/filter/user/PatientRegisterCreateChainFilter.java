package top.xblog1.emr.services.user.services.handler.filter.user;

import cn.hutool.core.lang.Validator;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;

import java.util.Objects;

/**
 * 患者注册参数校验
 */

public class PatientRegisterCreateChainFilter implements UserRegisterCreateChainFilter<UserRegisterReqDTO>{
    @Override
    public void handler(UserRegisterReqDTO requestParam) {
        if(requestParam.getUserType().equals(UserTypeEnum.PATIENT.code())){
            if(Objects.isNull(requestParam.getGender()))
                throw new ClientException(UserRegisterErrorCodeEnum.GENDER_NOTNULL);
            else if(requestParam.getIdCard().isEmpty())
                throw new ClientException(UserRegisterErrorCodeEnum.ID_CARD_NOTNULL);
            else if(!Validator.isCitizenId(requestParam.getIdCard()))
                throw new ClientException(UserRegisterErrorCodeEnum.ID_CARD_PATTERN_ERROR);
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
