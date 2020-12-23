package cn.carbank.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 幂等注解
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月16日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    /**
     * 锁的名称,支持SPEL表达式（默认为方法全名）
     * @return
     */
    String value() default "";

    /**
     * 锁的过期时间（单位毫秒）
     * @return
     */
    long lockExpireTime() default 10000;

    /**
     * 锁超时时间（单位毫秒）
     * @return
     */
    long tryTimeout() default 0;

    /**
     * 幂等Key,支持SPEL表达式（默认为方法名加参数MD5）
     * @return
     */
    String key() default "";

    /**
     * 触发幂等限制后调用的方法
     * @return
     */
    String idempotentMethod();

    /**
     * 当业务逻辑异常时，对指定异常不进行key存储
     * @return
     */
    Class<? extends Exception>[] noStorageFor() default {RuntimeException.class};

    /**
     * 自定义存储方式
     * @return
     */
    StorageParam[] storageParams() default {};
}
