package top.xblog1.emr.services.notice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.convention.result.Result;
import top.xblog1.emr.framework.starter.web.Results;
import top.xblog1.emr.services.notice.dto.req.NoticeCreateReqDTO;
import top.xblog1.emr.services.notice.dto.req.NoticePageQueryReqDTO;
import top.xblog1.emr.services.notice.dto.req.NoticeUpdateReqDTO;
import top.xblog1.emr.services.notice.dto.resp.NoticeCreateRespDTO;
import top.xblog1.emr.services.notice.dto.resp.NoticeQueryRespDTO;
import top.xblog1.emr.services.notice.dto.resp.NoticeUpdateRespDTO;
import top.xblog1.emr.services.notice.services.NoticeServices;

/**
 * 公告管理
 */
@RestController
@RequestMapping("/api/notice-services")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeServices noticeServices;
    /**
    * 创建公告
    * @param requestParam 
    * @return Result<NoticeCreateRespDTO> 
    */
    @PostMapping("/v1/create")
    public Result<NoticeCreateRespDTO> createNotice(@RequestBody NoticeCreateReqDTO requestParam) {
        return Results.success(noticeServices.create(requestParam));
    }
    /**
    * 删除公告
    * @param id 
    * @return Result<Void> 
    */
    @DeleteMapping("/v1/delete/{id}")
    public Result<Void> deleteNotice(@PathVariable @Valid Long id){
        noticeServices.delete(id);
        return Results.success();
    }
    /**
    * 修改公告
    * @param requestParam 
    * @return Result<NoticeUpdateRespDTO> 
    */
    @PutMapping("/v1/update")
    public Result<NoticeUpdateRespDTO> updateNotice(@RequestBody NoticeUpdateReqDTO requestParam){
        return Results.success(noticeServices.update(requestParam));
    }
    /**
    * 查询公告
    * @param id 
    * @return Result<NoticeQueryRespDTO> 
    */
    @GetMapping("/v1/queryById/{id}")
    public Result<NoticeQueryRespDTO> queryById(@PathVariable @Valid Long id){
        return Results.success(noticeServices.queryById(id));
    }

    /**
    * 分页查询
    * @param requestParam
    * @return Result<NoticeQueryRespDTO>
    */
    @GetMapping("/v1/pageQuery")
    public Result<PageResponse<NoticeQueryRespDTO>> pageQuery(@RequestBody NoticePageQueryReqDTO requestParam){
        return Results.success(noticeServices.pageQuery(requestParam));
    }
}
