package top.xblog1.emr.services.evaluation.config;

import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 */

@Data
@Component
@ConfigurationProperties(prefix = "baidu.api")
public class BaiduApiConfig {
    private String appId;
    private String apiKey;
    private String secretKey;
}
