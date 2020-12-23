package cn.carbank.configuration;

import cn.carbank.IdempotentAdvisor;
import cn.carbank.IdempotentInterceptor;
import cn.carbank.RedisLockClient;
import cn.carbank.RedisRepo;
import cn.carbank.config.DynamicConfigLoader;
import cn.carbank.config.IdempotentConfig;
import cn.carbank.locksupport.LockClient;
import cn.carbank.repository.IdempotentRecordRepo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

/**
 * 自动配置
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月23日
 */
@Configuration
@ConditionalOnProperty(value = "idempotent.enable", matchIfMissing = true)
@EnableConfigurationProperties(IdempotentProperties.class)
public class IdempotentConfiguration {

    @Bean("cn.carbank.IdempotentAdvisor")
    @ConditionalOnMissingBean(IdempotentAdvisor.class)
    public IdempotentAdvisor idempotentAdvisor() {
        return new IdempotentAdvisor();
    }

    @Bean("cn.carbank.IdempotentInterceptor")
    @ConditionalOnMissingBean(IdempotentInterceptor.class)
    public IdempotentInterceptor idempotentInterceptor(IdempotentProperties idempotentProperties) {
        return new IdempotentInterceptor(buildConfig(idempotentProperties));
    }

    private IdempotentConfig buildConfig(IdempotentProperties idempotentProperties) {
        IdempotentConfig config = DynamicConfigLoader.load();
        if (StringUtils.hasText(idempotentProperties.getGroupName())) {
            config.setGroupName(idempotentProperties.getGroupName());
        }
        if (idempotentProperties.getNamespace() != null) {
            config.setNamespace(idempotentProperties.getNamespace());
        }
        return config;
    }

    @Bean
    @ConditionalOnMissingBean(LockClient.class)
    public LockClient lockClient() {
        return new RedisLockClient();
    }

    @Bean
    @ConditionalOnMissingBean(IdempotentRecordRepo.class)
    public IdempotentRecordRepo idempotentRecordRepo() {
        return new RedisRepo();
    }

}
