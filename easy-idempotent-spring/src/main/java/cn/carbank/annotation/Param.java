package cn.carbank.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在jdk8前无法从class中获取参数名称
 * 因此需要添加指定注解以便获取到实际参数名称
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月16日
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
    String value();
}
