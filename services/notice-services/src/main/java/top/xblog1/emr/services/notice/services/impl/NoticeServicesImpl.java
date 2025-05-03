package top.xblog1.emr.services.notice.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.database.toolkit.PageUtil;
import top.xblog1.emr.services.notice.dao.entity.NoticeDO;
import top.xblog1.emr.services.notice.dao.mapper.NoticeMapper;
import top.xblog1.emr.services.notice.dto.req.NoticeCreateReqDTO;
import top.xblog1.emr.services.notice.dto.req.NoticePageQueryReqDTO;
import top.xblog1.emr.services.notice.dto.req.NoticeUpdateReqDTO;
import top.xblog1.emr.services.notice.dto.resp.NoticeCreateRespDTO;
import top.xblog1.emr.services.notice.dto.resp.NoticeQueryRespDTO;
import top.xblog1.emr.services.notice.dto.resp.NoticeUpdateRespDTO;
import top.xblog1.emr.services.notice.services.NoticeServices;

/**
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeServicesImpl implements NoticeServices {
    private final NoticeMapper noticeMapper;

    @Override
    public NoticeCreateRespDTO create(NoticeCreateReqDTO requestParam) {
        NoticeDO noticeDO = BeanUtil.convert(requestParam, NoticeDO.class);
        if(noticeDO.getAdminId() == null){
            throw new ClientException("管理员id不能为空");
        }else if(noticeDO.getAdminName().isEmpty()){
            throw new ClientException("管理员姓名不能为空");
        }else if (noticeDO.getTitle().isEmpty()){
            throw new ClientException("标题不能为空");
        }else if (noticeDO.getContent().isEmpty()){
            throw new ClientException("正文不能为空");
        }

        try {
            noticeMapper.insert(noticeDO);
        } catch (Exception e) {
            throw new ServiceException("新增公告失败");
        }
        return BeanUtil.convert(noticeDO, NoticeCreateRespDTO.class);
    }

    @Override
    public void delete(Long id) {
        NoticeDO noticeDO = noticeMapper.selectById(id);
        if(noticeDO == null){
            throw new ClientException("公告id不存在");
        }
        noticeDO.setUpdateTime(null);
        noticeMapper.updateById(noticeDO);
        noticeMapper.deleteById(id);
    }

    @Override
    public NoticeUpdateRespDTO update(NoticeUpdateReqDTO requestParam) {
        if(requestParam.getId() == null){
            throw new ClientException("公告id不能为空");
        }
        NoticeDO noticeDO = BeanUtil.convert(requestParam, NoticeDO.class);
        noticeMapper.updateById(noticeDO);
        return BeanUtil.convert(noticeMapper.selectById(noticeDO.getId()), NoticeUpdateRespDTO.class);
    }

    @Override
    public NoticeQueryRespDTO queryById(Long id) {
        if(id == null){
            throw new ClientException("公告id不能为空");
        }
        NoticeDO noticeDO = noticeMapper.selectById(id);
        if(noticeDO == null){
            throw new ClientException("公告"+id+"不存在");
        }
        return BeanUtil.convert(noticeDO, NoticeQueryRespDTO.class);
    }

    @Override
    public PageResponse<NoticeQueryRespDTO> pageQuery(NoticePageQueryReqDTO requestParam) {
        log.info(requestParam.toString());
        LambdaQueryWrapper<NoticeDO> queryWrapper = Wrappers.lambdaQuery(NoticeDO.class);
        if(requestParam.getAdminId() !=null)
            queryWrapper.eq(NoticeDO::getAdminId, requestParam.getAdminId());
        if(requestParam.getAdminName()!=null&& !requestParam.getAdminName().isEmpty())
            queryWrapper.like(NoticeDO::getAdminName, requestParam.getAdminName());
        if(requestParam.getTitle()!=null&& !requestParam.getTitle().isEmpty())
            queryWrapper.like(NoticeDO::getTitle,requestParam.getTitle());
        if(requestParam.getContent()!=null&& !requestParam.getContent().isEmpty())
            queryWrapper.like(NoticeDO::getContent,requestParam.getContent());
        queryWrapper.orderByDesc(NoticeDO::getUpdateTime);
        IPage<NoticeDO> noticeIPage =noticeMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        return PageUtil.convert(noticeIPage, each -> {

            return BeanUtil.convert(each, NoticeQueryRespDTO.class);
        });
    }
}
