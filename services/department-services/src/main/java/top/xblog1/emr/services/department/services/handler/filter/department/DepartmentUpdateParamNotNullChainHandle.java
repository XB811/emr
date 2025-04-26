package top.xblog1.emr.services.department.services.handler.filter.department;

import org.springframework.stereotype.Component;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.services.department.common.enums.DepartmentCreateErrorCodeEnum;
import top.xblog1.emr.services.department.dto.req.DepartmentUpdateReqDTO;

import java.util.Objects;

/**
 *
 */
@Component
public  class DepartmentUpdateParamNotNullChainHandle implements DepartmentUpdateChainFilter<DepartmentUpdateReqDTO>{
    @Override
    public void handler(DepartmentUpdateReqDTO requestParam) {
        if(Objects.equals(requestParam.getName(), ""))
            throw new ClientException(DepartmentCreateErrorCodeEnum.NAME_NOTNULL);
        else if(Objects.equals(requestParam.getDetail(), ""))
            throw new ClientException(DepartmentCreateErrorCodeEnum.DETAIL_NOTNULL);
        else if (Objects.equals(requestParam.getAddress(), ""))
            throw new ClientException(DepartmentCreateErrorCodeEnum.ADDRESS_NOTNULL);
        else if (Objects.equals(requestParam.getCode(), ""))
            throw new ClientException(DepartmentCreateErrorCodeEnum.CODE_NULL);
        else if(requestParam.getId().isEmpty())
            throw new ClientException(DepartmentCreateErrorCodeEnum.ID_NOTNULL);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
