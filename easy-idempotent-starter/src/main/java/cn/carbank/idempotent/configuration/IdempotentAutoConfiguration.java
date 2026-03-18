/*
 * Copyright 2020 chengzhengZhou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.carbank.idempotent.configuration;

import cn.carbank.idempotent.IdempotentAdvisor;
import cn.carbank.idempotent.IdempotentInterceptor;
import cn.carbank.idempotent.locksupport.RedisLockClient;
import cn.carbank.idempotent.repository.RedisRepoImpl;
import cn.carbank.idempotent.locksupport.LockClient;
import cn.carbank.idempotent.repository.IdempotentRecordRepo;
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
 * @author chengzhengZhou
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
