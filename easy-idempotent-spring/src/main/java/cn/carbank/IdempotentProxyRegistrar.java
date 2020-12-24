package cn.carbank;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * 幂等代理注册
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月16日
 */
public class IdempotentProxyRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean present = ClassUtils.isPresent("cn.carbank.configuration.IdempotentAutoConfiguration", resourceLoader.getClassLoader());
        if (present) {
            return;
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(IdempotentAdvisor.class);
        registry.registerBeanDefinition(IdempotentAdvisor.class.getName(), builder.getBeanDefinition());

        BeanDefinitionBuilder builder1 = BeanDefinitionBuilder.genericBeanDefinition(IdempotentInterceptor.class);
        registry.registerBeanDefinition(IdempotentInterceptor.class.getName(), builder1.getBeanDefinition());
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
