package cn.carbank.idempotent;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * 幂等代理注册
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月16日
 */
public class IdempotentProxyRegistrar implements ImportBeanDefinitionRegistrar, BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean present = ClassUtils.isPresent("cn.carbank.configuration.IdempotentAutoConfiguration", classLoader);
        if (present) {
            return;
        }
        BeanDefinitionBuilder builder1 = BeanDefinitionBuilder.genericBeanDefinition(IdempotentInterceptor.class);
        registry.registerBeanDefinition("idempotentInterceptor", builder1.getBeanDefinition());

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(IdempotentAdvisor.class);
        registry.registerBeanDefinition("idempotentAdvisor", builder.getBeanDefinition());
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
