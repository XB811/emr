package top.xblog1.emr.services.notice.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.services.notice.dao.entity.NoticeDO;
import top.xblog1.emr.services.notice.dao.mapper.NoticeMapper;
import top.xblog1.emr.services.notice.dto.req.NoticeCreateReqDTO;
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
}
