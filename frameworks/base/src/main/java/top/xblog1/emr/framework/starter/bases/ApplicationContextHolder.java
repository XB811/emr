package top.xblog1.emr.framework.starter.bases;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.util.Map;
import java.lang.annotation.Annotation;
/**
 * Application context holder.
 * 依赖 Spring 提供的 ApplicationContextAware，来将 Spring IOC 容器的对象放到一个自定义容器中，并持有 Spring IOC 容器。
 * 这样就可以通过自定义容器访问 Spring IOC 容器获取 Spring Bean。
 */

public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext CONTEXT;

    /**
    * 设置应用上下文
    */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.CONTEXT = applicationContext;
    }

    /**
     * Get ioc container bean by type.
     * 通过类名获取ioc容器中的bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return CONTEXT.getBean(clazz);
    }

    /**
     * Get ioc container bean by name.
     * 通过变量名获取ioc容器中的bean
     */
    public static Object getBean(String name) {
        return CONTEXT.getBean(name);
    }

    /**
     * Get ioc container bean by name and type.
     * 通过类名和变量名获取bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return CONTEXT.getBean(name, clazz);
    }

    /**
     * Get a set of ioc container beans by type.
     * 按类型获取一组 ioc 容器 bean。
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return CONTEXT.getBeansOfType(clazz);
    }

    /**
     * Find whether the bean has annotations.
     * 查找 bean 是否有注解
     *
     */
    public static <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
        return CONTEXT.findAnnotationOnBean(beanName, annotationType);
    }

    /**
     * Get application context.
     * 获取应用程序上下文。
     */
    public static ApplicationContext getInstance() {
        return CONTEXT;
    }
}
