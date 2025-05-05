package top.xblog1.emr.services.evaluation.toolkit;


import org.json.JSONArray;
import org.json.JSONObject;
import com.baidu.aip.nlp.AipNlp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.xblog1.emr.services.evaluation.config.BaiduApiConfig;
import top.xblog1.emr.services.evaluation.toolkit.SentimentResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 百度NLP API工具类
 */
@Component
@Slf4j
public class BaiduNLPApi {

    @Autowired
    private BaiduApiConfig baiduApiConfig;

    private static AipNlp client;

    // 传入可选参数调用接口
    private HashMap<String, Object> options = new HashMap<String, Object>();

    /**
     * 情感倾向分析
     * @param text 待分析文本
     * @return 情感分析结果，如果分析失败或无内容返回null
     */
    public SentimentResult sentimentClassify(String text) {
        if (client == null) {
            // 如果client为空
            client = new AipNlp(baiduApiConfig.getAppId(), baiduApiConfig.getApiKey(), baiduApiConfig.getSecretKey());}
        // 检查文本是否有效
        if (text == null || text.isEmpty()) {
            log.warn("输入文本为空，无法进行情感分析");
            return null;
        }

        try {
            // 调用API进行情感分析
            JSONObject res = client.sentimentClassify(text, options);
            log.info("情感分析API返回: {}", res);

            // 检查返回结果是否有效
            if (res != null && !res.has("error_code")) {
                SentimentResult result = new SentimentResult();
                result.setText(res.getString("text"));

                // 解析items数组
                if (res.has("items") && res.get("items") instanceof JSONArray) {
                    JSONArray itemArray = res.getJSONArray("items");
                    List<SentimentResult.SentimentItem> sentimentItems = new ArrayList<>();

                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject item = itemArray.getJSONObject(i);
                        SentimentResult.SentimentItem sentimentItem = new SentimentResult.SentimentItem();
                        sentimentItem.setSentiment(item.getInt("sentiment"));
                        sentimentItem.setConfidence(item.getDouble("confidence"));
                        sentimentItem.setPositive_prob(item.getDouble("positive_prob"));
                        sentimentItem.setNegative_prob(item.getDouble("negative_prob"));
                        sentimentItems.add(sentimentItem);
                    }

                    result.setItems(sentimentItems);
                    return result;
                }
            } else {
                // API调用出错
                log.error("情感分析API调用失败: {}", res);
            }
        } catch (Exception e) {
            log.error("情感分析过程中发生异常", e);
        }

        return null;
    }

    /**
     * 获取文本情感值 (简化方法)
     * 0 - 消极
     * 1 - 中性
     * 2 - 积极
     * -1 - 分析失败
     */
    public Integer getSentiment(String text) {
        SentimentResult result = sentimentClassify(text);
        if (result != null && result.getItems() != null && !result.getItems().isEmpty()) {
            return result.getItems().get(0).getSentiment();
        }
        return -1;
    }
    /**
    * 生成五级评分
    * @param text
    * @return Integer
    */
    public Integer getRating(String text){
        SentimentResult result = sentimentClassify(text);
        if (result != null && result.getItems() != null && !result.getItems().isEmpty()) {
            if(result.getItems().get(0).getPositive_prob()>0.8)return 5;
            else if(result.getItems().get(0).getPositive_prob()>0.6)return 4;
            else if(result.getItems().get(0).getPositive_prob()>0.4)return 3;
            else if(result.getItems().get(0).getPositive_prob()>0.2)return 2;
            else return 1;
        }
        return -1;
    }
}