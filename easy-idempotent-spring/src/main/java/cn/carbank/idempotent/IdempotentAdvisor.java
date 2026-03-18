package cn.carbank.idempotent;

import cn.carbank.idempotent.annotation.Idempotent;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * advisor
 *
 * @author chengzhengZhou
 * @since 2020年12月16日
 */
public class IdempotentAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    public IdempotentAdvisor() {
        setAdviceBeanName("cn.carbank.IdempotentInterceptor");
    }

    @Override
    public Pointcut getPointcut() {
        return AnnotationMatchingPointcut.forMethodAnnotation(Idempotent.class);
    }
}
