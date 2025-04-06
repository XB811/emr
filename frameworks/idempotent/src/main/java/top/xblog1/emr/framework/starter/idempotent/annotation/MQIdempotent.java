package top.xblog1.emr.framework.starter.idempotent.annotation;

import top.xblog1.emr.framework.starter.idempotent.enums.IdempotentSceneEnum;
import top.xblog1.emr.framework.starter.idempotent.enums.IdempotentTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MQ 业务场景幂等注解
 */
@Deprecated
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Idempotent(scene = IdempotentSceneEnum.MQ)
public @interface MQIdempotent {

    /**
     * {@link Idempotent#key} 的别名
     */
    @AliasFor(annotation = Idempotent.class, attribute = "key")
    String key() default "";

    /**
     * {@link Idempotent#type} 的别名
     */
    @AliasFor(annotation = Idempotent.class, attribute = "type")
    IdempotentTypeEnum type() default IdempotentTypeEnum.SPEL;
}
