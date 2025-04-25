package top.xblog1.emr.services.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 用户注册用户名布隆过滤器属性配置
 * 只用于患者
 */
@Data
@ConfigurationProperties(prefix = PatientRegisterPhoneBloomFilterProperties.PREFIX)
public final class PatientRegisterPhoneBloomFilterProperties {
    public static final String PREFIX = "framework.cache.redis.bloom-filter.user-register.phone";
    /**
     * 用户注册布隆过滤器实例名称
     */
    private String name = "patient_register_phone_cache_penetration_bloom_filter";

    /**
     * 每个元素的预期插入量
     */
    private Long expectedInsertions = 16L;

    /**
     * 预期错误概率
     */
    private Double falseProbability = 0.03D;
}
