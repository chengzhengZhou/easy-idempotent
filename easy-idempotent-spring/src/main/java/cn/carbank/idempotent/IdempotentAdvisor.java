package cn.carbank.idempotent;

import cn.carbank.idempotent.annotation.Idempotent;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * advisor
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月16日
 */
public class IdempotentAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    public IdempotentAdvisor() {
        setAdviceBeanName("idempotentInterceptor");
    }

    @Override
    public Pointcut getPointcut() {
        return AnnotationMatchingPointcut.forMethodAnnotation(Idempotent.class);
    }
}
