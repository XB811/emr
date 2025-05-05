package top.xblog1.emr.services.evaluation.toolkit;

import lombok.Data;

/**
 *
 */
import lombok.Data;
import java.util.List;

@Data
public class SentimentResult {
    private String text;
    private List<SentimentItem> items;

    @Data
    public static class SentimentItem {
        private Integer sentiment;    // 情感极性分类结果
        private Double confidence;    // 分类的置信度
        private Double positive_prob; // 属于积极类别的概率
        private Double negative_prob; // 属于消极类别的概率
    }
}
