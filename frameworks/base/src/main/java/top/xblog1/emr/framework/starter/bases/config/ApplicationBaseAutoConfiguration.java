package top.xblog1.emr.framework.starter.bases.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import top.xblog1.emr.framework.starter.bases.ApplicationContextHolder;
import top.xblog1.emr.framework.starter.bases.init.ApplicationContentPostProcessor;
import top.xblog1.emr.framework.starter.bases.safe.FastJsonSafeMode;

/**
 *  应用基础自动装配
 *
 */

public class ApplicationBaseAutoConfiguration {
    /**
    * 如果配置文件中配置了 framework.fastjson.safe-mode=true 那么则开启安全模式，反之无任何变化。
    * @return FastJsonSafeMode
    */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "framework.fastjson.safe-mode", havingValue = "true")
    public FastJsonSafeMode congoFastJsonSafeMode() {
        return new FastJsonSafeMode();
    }
    /**
    * Application context holder.
    * 依赖 Spring 提供的 ApplicationContextAware，来将 Spring IOC 容器的对象放到一个自定义容器中，并持有 Spring IOC 容器。
    * 这样就可以通过自定义容器访问 Spring IOC 容器获取 Spring Bean。
    * @return ApplicationContextHolder
    */
    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder congoApplicationContextHolder() {
        return new ApplicationContextHolder();
    }
    /**
    * 应用初始化
     * 保证spring事件只被执行一次
    * @param applicationContext
    * @return ApplicationContentPostProcessor
    */
    @Bean
    @ConditionalOnMissingBean
    public ApplicationContentPostProcessor congoApplicationContentPostProcessor(ApplicationContext applicationContext) {
        return new ApplicationContentPostProcessor(applicationContext);
    }
}
