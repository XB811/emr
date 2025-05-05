package top.xblog1.emr.services.evaluation.listener;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.xblog1.emr.framework.starter.cache.DistributedCache;
import top.xblog1.emr.framework.starter.convention.exception.ServiceException;
import top.xblog1.emr.services.evaluation.dao.entity.EvaluationDO;
import top.xblog1.emr.services.evaluation.dao.mapper.EvaluationMapper;
import top.xblog1.emr.services.evaluation.toolkit.BaiduNLPApi;

/**
 * rabbitmq接受类
 */
@Component
@RequiredArgsConstructor
public class SentimentListener {
    private  final BaiduNLPApi baiduNLPApi;
    private final DistributedCache distributedCache;
    private final EvaluationMapper evaluationMapper;

    String EVALUATION_CACHE ="emr-evaluation-service:evaluation_rating:";

    @RabbitListener(queues = "emr.evaluation")
    public void listenEvaluationRating(Long evaluationId) {
        //先查询redis获取id对应的评价内容
        String s = distributedCache.get(EVALUATION_CACHE + evaluationId.toString(), String.class);
        distributedCache.delete(EVALUATION_CACHE + evaluationId);
        if(s==null){
            throw new ServiceException("评价情感分析失败");
        }
        EvaluationDO bean = JSONUtil.toBean(s, EvaluationDO.class);
        //调用api服务
        if(bean!=null){
            bean.setRating(baiduNLPApi.getRating(bean.getContent()));
        }
        //存入数据库
        evaluationMapper.updateById(bean);

    }
}
