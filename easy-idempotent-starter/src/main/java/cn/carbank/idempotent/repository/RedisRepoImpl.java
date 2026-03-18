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
package cn.carbank.idempotent.repository;

import cn.carbank.idempotent.IdempotentRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * redis存储
 *
 * @author chengzhengZhou
 * @since 2020年12月21日
 */
public class RedisRepoImpl implements IdempotentRecordRepo {
    private final Logger logger = LoggerFactory.getLogger(RedisRepoImpl.class);
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean add(String key, String value, int expireTime, TimeUnit timeUnit) {
        if (logger.isDebugEnabled()) {
            logger.debug("redis add key:{} expireTime:{}", key, expireTime);
        }
        if (expireTime <= 0) {
            return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
        } else {
            return stringRedisTemplate.opsForValue().setIfAbsent(key, value, expireTime, timeUnit);
        }
    }

    @Override
    public boolean exist(String s) {
        if (logger.isDebugEnabled()) {
            logger.debug("redis is exist {}", s);
        }
        return stringRedisTemplate.hasKey(s);
    }

    @Override
    public IdempotentRecord get(String s) {
        return null;
    }

    @Override
    public boolean delete(String s) {
        return false;
    }
}
