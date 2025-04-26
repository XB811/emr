package top.xblog1.emr.services.department.services.handler.filter.department;

import top.xblog1.emr.framework.starter.designpattern.chain.AbstractChainHandler;
import top.xblog1.emr.services.department.common.enums.DepartmentChainMarkEnum;
import top.xblog1.emr.services.department.dto.req.DepartmentInsertReqDTO;

/**
 *
 */

public interface DepartmentCreateChainFilter<T extends DepartmentInsertReqDTO> extends AbstractChainHandler<DepartmentInsertReqDTO> {
    @Override
    default String mark() {
        return DepartmentChainMarkEnum.DEPARTMENT_CREATE_FILTER.name();
    }
}
