package cn.carbank;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 幂等代理注册
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月16日
 */
public class IdempotentProxyRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(IdempotentAdvisor.class);
        registry.registerBeanDefinition(IdempotentAdvisor.class.getName(), builder.getBeanDefinition());

        BeanDefinitionBuilder builder1 = BeanDefinitionBuilder.genericBeanDefinition(IdempotentInterceptor.class);
        registry.registerBeanDefinition(IdempotentInterceptor.class.getName(), builder1.getBeanDefinition());
    }
}
