package top.xblog1.emr.services.user.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 布隆过滤器配置
 */
@Configuration
@EnableConfigurationProperties({PatientRegisterUsernameBloomFilterProperties.class, PatientRegisterPhoneBloomFilterProperties.class})
public class RBloomFilterConfiguration {

    /**
     * 防止用户注册缓存穿透的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> patientRegisterUsernameCachePenetrationBloomFilter(RedissonClient redissonClient, PatientRegisterUsernameBloomFilterProperties patientRegisterUsernameBloomFilterProperties) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter(patientRegisterUsernameBloomFilterProperties.getName());
        cachePenetrationBloomFilter.tryInit(patientRegisterUsernameBloomFilterProperties.getExpectedInsertions(), patientRegisterUsernameBloomFilterProperties.getFalseProbability());
        return cachePenetrationBloomFilter;
    }

    /**
    * 用户注册 手机号布隆过滤器
    */
    @Bean
    public RBloomFilter<String> patientRegisterPhoneCachePenetrationBloomFilter(RedissonClient redissonClient,PatientRegisterPhoneBloomFilterProperties patientRegisterPhoneBloomFilterProperties){
        RBloomFilter<String> cachePenetrationBloomFilter =redissonClient.getBloomFilter(patientRegisterPhoneBloomFilterProperties.getName());
        cachePenetrationBloomFilter.tryInit(patientRegisterPhoneBloomFilterProperties.getExpectedInsertions(), patientRegisterPhoneBloomFilterProperties.getFalseProbability());
        return cachePenetrationBloomFilter;
    }
}
