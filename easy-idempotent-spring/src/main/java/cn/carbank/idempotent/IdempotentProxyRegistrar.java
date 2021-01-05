package cn.carbank.idempotent;

import cn.carbank.idempotent.repository.RedisIdempotentRepoImpl;
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
        BeanDefinitionBuilder idempotentInterceptorDefinition = BeanDefinitionBuilder.genericBeanDefinition(IdempotentInterceptor.class);
        registry.registerBeanDefinition("idempotentInterceptor", idempotentInterceptorDefinition.getBeanDefinition());

        BeanDefinitionBuilder idempotentAdvisorDefinition = BeanDefinitionBuilder.genericBeanDefinition(IdempotentAdvisor.class);
        registry.registerBeanDefinition("idempotentAdvisor", idempotentAdvisorDefinition.getBeanDefinition());

        if (!registry.containsBeanDefinition("redisLockClient")) {
            BeanDefinitionBuilder lockClientDefinition = BeanDefinitionBuilder.genericBeanDefinition(RedisLockClient.class);
            lockClientDefinition.setLazyInit(true);
            registry.registerBeanDefinition("redisLockClient", lockClientDefinition.getBeanDefinition());
        }
        if (!registry.containsBeanDefinition("redisIdempotentRepoImpl")) {
            BeanDefinitionBuilder repoDefinition = BeanDefinitionBuilder.genericBeanDefinition(RedisIdempotentRepoImpl.class);
            repoDefinition.setLazyInit(true);
            registry.registerBeanDefinition("redisIdempotentRepoImpl", repoDefinition.getBeanDefinition());
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
