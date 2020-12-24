package cn.carbank.configuration;

import cn.carbank.IdempotentAdvisor;
import cn.carbank.IdempotentInterceptor;
import cn.carbank.locksupport.LockClient;
import cn.carbank.locksupport.RedisLockClient;
import cn.carbank.repository.IdempotentRecordRepo;
import cn.carbank.repository.RedisRepoImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 自动配置
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月23日
 */
@Configuration
@ConditionalOnProperty(value = "idempotent.enable", matchIfMissing = true)
@EnableConfigurationProperties(IdempotentProperties.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class IdempotentAutoConfiguration {

    @Bean("cn.carbank.IdempotentAdvisor")
    @ConditionalOnMissingBean(IdempotentAdvisor.class)
    public IdempotentAdvisor idempotentAdvisor() {
        return new IdempotentAdvisor();
    }

    @Bean("cn.carbank.IdempotentInterceptor")
    @ConditionalOnMissingBean(IdempotentInterceptor.class)
    public IdempotentInterceptor idempotentInterceptor(IdempotentProperties idempotentProperties) {
        return new IdempotentInterceptor(ConfigSetter.of(idempotentProperties));
    }

    @Bean
    @ConditionalOnMissingBean(LockClient.class)
    @ConditionalOnBean(StringRedisTemplate.class)
    public LockClient lockClient() {
        return new RedisLockClient();
    }

    @Bean
    @ConditionalOnMissingBean(RedisRepoImpl.class)
    @ConditionalOnBean(StringRedisTemplate.class)
    public IdempotentRecordRepo idempotentRecordRepo() {
        return new RedisRepoImpl();
    }

}
