package top.xblog1.emr.services.evaluation.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import top.xblog1.emr.framework.starter.cache.DistributedCache;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.framework.starter.convention.page.PageResponse;
import top.xblog1.emr.framework.starter.database.toolkit.PageUtil;
import top.xblog1.emr.services.evaluation.dao.entity.DoctorRatingDO;
import top.xblog1.emr.services.evaluation.dao.entity.EvaluationDO;
import top.xblog1.emr.services.evaluation.dao.mapper.DoctorRatingMapper;
import top.xblog1.emr.services.evaluation.dao.mapper.EvaluationMapper;
import top.xblog1.emr.services.evaluation.dto.req.EvaluationCreateReqDTO;
import top.xblog1.emr.services.evaluation.dto.req.EvaluationPageQueryReqDTO;
import top.xblog1.emr.services.evaluation.dto.req.EvaluationUpdateReqDTO;
import top.xblog1.emr.services.evaluation.dto.resp.EvaluationCreateRespDTO;
import top.xblog1.emr.services.evaluation.dto.resp.EvaluationQueryRespDTO;
import top.xblog1.emr.services.evaluation.services.EvaluationServices;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class EvaluationServicesImpl implements EvaluationServices {
    private final EvaluationMapper evaluationMapper;
    private final RabbitTemplate rabbitTemplate;
    private final DistributedCache distributedCache;
    private final DoctorRatingMapper   doctorRatingMapper;

    String EVALUATION_CACHE ="emr-evaluation-service:evaluation_rating:";
    @Override
    public EvaluationCreateRespDTO create(EvaluationCreateReqDTO requestParam) {
        if(requestParam.getPatientId()==null)
            throw new ClientException("患者Id为空");
        else if(requestParam.getDoctorId()==null)
            throw new ClientException("医生Id为空");
        else if(requestParam.getEmrId()==null)
            throw new ClientException("Emr Id为空");
        EvaluationDO evaluationDO = BeanUtil.convert(requestParam, EvaluationDO.class);
        try{
            evaluationMapper.insert(evaluationDO);
            // 将数据存入redis
            distributedCache.put(EVALUATION_CACHE+evaluationDO.getId(),evaluationDO);
            //发送到消息队列
            rabbitTemplate.convertAndSend("emr.evaluation",evaluationDO.getId());
        }catch (DuplicateKeyException ex){
            throw new ClientException("emrId重复");
        }catch (Exception ex){
            throw new ServiceException("评价新增失败");
        }
        return BeanUtil.convert(evaluationDO, EvaluationCreateRespDTO.class);
    }

    @Override
    public void delete(Long id) {
        EvaluationDO evaluationDO = evaluationMapper.selectById(id);
        if(evaluationDO==null)
            throw new ClientException("评价id不存在");
        evaluationDO.setUpdateTime(null);
        evaluationMapper.updateById(evaluationDO);
        evaluationMapper.deleteById(id);
    }

    @Override
    public EvaluationQueryRespDTO queryById(Long id) {
        EvaluationDO evaluationDO = evaluationMapper.selectById(id);
        if(evaluationDO==null)
            throw new ClientException("评价不存在");
        return BeanUtil.convert(evaluationDO, EvaluationQueryRespDTO.class);
    }

    @Override
    public EvaluationQueryRespDTO queryByEmrId(Long emrId) {
        LambdaQueryWrapper<EvaluationDO> queryWrapper = Wrappers.lambdaQuery(EvaluationDO.class)
                .eq(EvaluationDO::getEmrId, emrId);
        EvaluationDO evaluationDO = evaluationMapper.selectOne(queryWrapper);
        if(evaluationDO==null)
            throw new ClientException("评价不存在");
        return BeanUtil.convert(evaluationDO, EvaluationQueryRespDTO.class);
    }

    @Override
    public void update(EvaluationUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<EvaluationDO> updateWrapper = Wrappers.lambdaUpdate(EvaluationDO.class)
                .eq(EvaluationDO::getId,requestParam.getId());
        try {
            evaluationMapper.updateById(BeanUtil.convert(requestParam, EvaluationDO.class));
        } catch (Exception e) {
            throw new ServiceException("更新评价失败");
        }

    }

    @Override
    public PageResponse<EvaluationQueryRespDTO> pageQuery(EvaluationPageQueryReqDTO requestParam) {
        LambdaQueryWrapper<EvaluationDO> queryWrapper = Wrappers.lambdaQuery(EvaluationDO.class);
        if(requestParam.getPatientId()!=null)
            queryWrapper.eq(EvaluationDO::getPatientId,requestParam.getPatientId());
        if(requestParam.getDoctorId()!=null)
            queryWrapper.eq(EvaluationDO::getDoctorId,requestParam.getDoctorId());
        queryWrapper.orderByDesc(EvaluationDO::getUpdateTime);
        IPage<EvaluationDO> evaluationDOIPage = evaluationMapper.selectPage(PageUtil.convert(requestParam),queryWrapper);
        return PageUtil.convert(evaluationDOIPage,each ->{
            return BeanUtil.convert(each, EvaluationQueryRespDTO.class);
        });
    }

    @Override
    public Boolean hasEvaluation(String emrId) {
        LambdaQueryWrapper<EvaluationDO> queryWrapper = Wrappers.lambdaQuery(EvaluationDO.class)
                .eq(EvaluationDO::getEmrId,emrId);
        if(evaluationMapper.selectCount(queryWrapper)>0)
            return true;
        return false;
    }

    @Override
    public Double getAverageRating(String doctorId) {
        LambdaQueryWrapper<DoctorRatingDO> queryWrapper = Wrappers.lambdaQuery(DoctorRatingDO.class)
                .eq(DoctorRatingDO::getDoctorId,doctorId);
        //查询数据库
        DoctorRatingDO doctorRatingDO = doctorRatingMapper.selectOne(queryWrapper);
        if(doctorRatingDO!=null){
            return doctorRatingDO.getAverageRating();
        }
        return -1.0;
    }
}
