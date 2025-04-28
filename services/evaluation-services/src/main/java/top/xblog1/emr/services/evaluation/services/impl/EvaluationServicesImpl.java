package top.xblog1.emr.services.evaluation.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import top.xblog1.emr.framework.starter.common.toolkit.BeanUtil;
import top.xblog1.emr.framework.starter.convention.exception.ClientException;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.services.evaluation.dao.entity.EvaluationDO;
import top.xblog1.emr.services.evaluation.dao.mapper.EvaluationMapper;
import top.xblog1.emr.services.evaluation.dto.req.EvaluationCreateReqDTO;
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
}
