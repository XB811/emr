package top.xblog1.emr.framework.starter.bases.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 应用初始化后置处理器，防止Spring事件被多次执行
 */
@RequiredArgsConstructor
public class ApplicationContentPostProcessor implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationContext applicationContext;

    /**
     * 执行标识，确保Spring事件 {@link ApplicationReadyEvent} 有且执行一次
     */
    private final AtomicBoolean executeOnlyOnce = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        /**
        * 如果Spring事件 {@link ApplicationReadyEvent}没有执行过，executeOnlyOnce{@link executeOnlyOnce}由false变为true
        * 并继续执行应用初始化事件{@link ApplicationInitializingEvent}
        */
        if (!executeOnlyOnce.compareAndSet(false, true)) {
            return;
        }
        applicationContext.publishEvent(new ApplicationInitializingEvent(this));
    }
}