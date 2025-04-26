package top.xblog1.emr.services.department.services.handler.filter.department;

import org.springframework.stereotype.Component;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.services.department.common.enums.DepartmentCreateErrorCodeEnum;
import top.xblog1.emr.services.department.dto.req.DepartmentInsertReqDTO;

/**
 * 部门创建参数校验
 */
@Component
public class DepartmentCreateParamNotNullChainHandler implements DepartmentCreateChainFilter<DepartmentInsertReqDTO>{
    @Override
    public void handler(DepartmentInsertReqDTO requestParam) {
        if(requestParam.getName().isEmpty())
            throw new ClientException(DepartmentCreateErrorCodeEnum.NAME_NOTNULL);
        else if(requestParam.getDetail().isEmpty())
            throw new ClientException(DepartmentCreateErrorCodeEnum.DETAIL_NOTNULL);
        else if (requestParam.getAddress().isEmpty())
            throw new ClientException(DepartmentCreateErrorCodeEnum.ADDRESS_NOTNULL);
        else if (requestParam.getCode().isEmpty())
            throw new ClientException(DepartmentCreateErrorCodeEnum.CODE_NULL);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
