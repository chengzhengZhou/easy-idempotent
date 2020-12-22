package cn.carbank.repository;

import cn.carbank.IdempotentRecord;
import cn.carbank.constant.DateField;
import cn.carbank.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis存储
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public class RedisIdempotentRecordRepoImpl implements IdempotentRecordRepo {
    /** 日志记录器 */
    private final Logger logger = LoggerFactory.getLogger(RedisIdempotentRecordRepoImpl.class);

    Map<String, IdempotentRecord> cache = new HashMap<>();

    @Override
    public boolean add(String key, String value, int expireTime, TimeUnit timeUnit) {
        logger.debug("redis add {}", key);
        IdempotentRecord idempotentRecord = new IdempotentRecord();
        idempotentRecord.setKey(key);
        idempotentRecord.setValue(value);
        Date now = new Date();
        idempotentRecord.setAddTime(now);
        if (expireTime > 0) {
            Date offset = DateUtil.offset(now, DateField.of(timeUnit), expireTime);
            idempotentRecord.setExpireTime(offset);
        }
        cache.put(key, idempotentRecord);
        return true;
    }

    @Override
    public boolean exist(String key) {
        logger.debug("redis exist {}", key);
        return cache.containsKey(key);
    }

    @Override
    public IdempotentRecord get(String key) {
        logger.debug("redis get {}", key);
        return cache.get(key);
    }

    @Override
    public boolean delete(String key) {
        logger.error("redis delete {}", key);
        cache.remove(key);
        return true;
    }

}
