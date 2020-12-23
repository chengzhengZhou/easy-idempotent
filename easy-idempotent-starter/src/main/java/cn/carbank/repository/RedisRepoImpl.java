package cn.carbank.repository;

import cn.carbank.IdempotentRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 请填写类注释
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
        logger.error("redis add {}", key);
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, expireTime, timeUnit);
    }

    @Override
    public boolean exist(String s) {
        logger.error("redis exit {}", s);
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
