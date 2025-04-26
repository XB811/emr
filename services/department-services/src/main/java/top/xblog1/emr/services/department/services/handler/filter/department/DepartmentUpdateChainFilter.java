package top.xblog1.emr.services.department.services.handler.filter.department;

import top.xblog1.emr.framework.starter.designpattern.chain.AbstractChainHandler;
import top.xblog1.emr.services.department.common.enums.DepartmentChainMarkEnum;
import top.xblog1.emr.services.department.dto.req.DepartmentInsertReqDTO;
import top.xblog1.emr.services.department.dto.req.DepartmentUpdateReqDTO;

/**
 *
 */

public interface DepartmentUpdateChainFilter <T extends DepartmentUpdateReqDTO> extends AbstractChainHandler<DepartmentUpdateReqDTO> {
    @Override
    default String mark() {
        return DepartmentChainMarkEnum.DEPARTMENT_UPDATE_FILTER.name();
    }
}
