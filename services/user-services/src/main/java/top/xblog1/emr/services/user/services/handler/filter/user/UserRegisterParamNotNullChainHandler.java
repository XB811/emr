package top.xblog1.emr.services.user.services.handler.filter.user;

import cn.hutool.core.lang.Validator;
import org.springframework.stereotype.Component;
import top.xblog1.emr.framework.starter.common.enums.UserTypeEnum;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.services.user.common.enums.UserRegisterErrorCodeEnum;
import top.xblog1.emr.services.user.dto.req.UserRegisterReqDTO;
import top.xblog1.emr.services.user.dto.strategy.BaseUserDTO;

import java.util.Objects;

/**
 * 管理员注册参数校验
 * 公有信息注册参数校验
 */
@Component
public class UserRegisterParamNotNullChainHandler implements UserRegisterCreateChainFilter<UserRegisterReqDTO>{

    @Override
    public void handler(UserRegisterReqDTO requestParam) {
        if(requestParam.getUserType().isEmpty())
            throw new ClientException(UserRegisterErrorCodeEnum.USER_TYPE_NOTNULL);
        else if(requestParam.getUsername().isEmpty())
            throw new ClientException(UserRegisterErrorCodeEnum.USER_NAME_NOTNULL);
        else if(requestParam.getPassword().isEmpty())
            throw new ClientException(UserRegisterErrorCodeEnum.PASSWORD_NOTNULL);
        else if(requestParam.getRealName().isEmpty())
            throw new ClientException(UserRegisterErrorCodeEnum.REAL_NAME_NOTNULL);
        else if(requestParam.getPhone().isEmpty())
            throw new ClientException(UserRegisterErrorCodeEnum.PHONE_NOTNULL);
        else if(!Validator.isMobile(requestParam.getPhone()))
            throw new ClientException(UserRegisterErrorCodeEnum.PHONE_PATTERN_ERROR);

    }
    @Override
    public int getOrder() {
        return 0;
    }
}
