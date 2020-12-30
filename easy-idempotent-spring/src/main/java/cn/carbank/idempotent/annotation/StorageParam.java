package cn.carbank.idempotent.annotation;

import cn.carbank.idempotent.constant.StorageType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 存储配置
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月16日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StorageParam {

    /**
     * 存储工具类型
     * @return
     */
    StorageType storage();

    /**
     * 存储时间（默认为永久）
     * @return
     */
    int expireTime() default 0;

    /**
     * 时间单位
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
