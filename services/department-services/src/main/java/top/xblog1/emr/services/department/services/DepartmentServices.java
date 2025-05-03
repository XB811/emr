package top.xblog1.emr.services.department.services;

import jakarta.validation.Valid;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.services.department.dto.req.DepartmentInsertReqDTO;
import top.xblog1.emr.services.department.dto.req.DepartmentPageQueryReqDTO;
import top.xblog1.emr.services.department.dto.req.DepartmentUpdateReqDTO;
import top.xblog1.emr.services.department.dto.resp.DepartmentQueryRespDTO;
import top.xblog1.emr.services.department.dto.resp.DepartmentUpdateRespDTO;

import java.util.List;

/**
 *
 */

public interface DepartmentServices {
    Long create(DepartmentInsertReqDTO requestParam);

    void delete(Long id);

    DepartmentUpdateRespDTO update(@Valid DepartmentUpdateReqDTO requestParam);

    DepartmentQueryRespDTO queryById(@Valid Long id);

    List<DepartmentQueryRespDTO> queryAll();

    PageResponse<DepartmentQueryRespDTO> pageQuery(DepartmentPageQueryReqDTO requestParam);
}
