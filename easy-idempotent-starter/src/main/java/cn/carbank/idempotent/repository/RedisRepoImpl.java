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
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
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
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, expireTime, timeUnit);
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
