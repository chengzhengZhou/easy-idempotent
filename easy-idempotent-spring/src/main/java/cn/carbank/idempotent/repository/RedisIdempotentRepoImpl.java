package cn.carbank.idempotent.repository;

import cn.carbank.idempotent.IdempotentRecord;
import cn.carbank.idempotent.config.DynamicConfigLoader;
import cn.carbank.idempotent.config.IdempotentConfig;
import com.zuche.redis.core.ValueCommands;
import com.zuche.redis.factory.RedisFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * redis存储
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2021年01月05日
 */
@Lazy
public class RedisIdempotentRepoImpl implements IdempotentRecordRepo, InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(RedisIdempotentRepoImpl.class);
    private ValueCommands commands;
    private int nameSpace = 1;

    @Override
    public boolean add(String key, String value, int expireTime, TimeUnit timeUnit) {
        if (logger.isDebugEnabled()) {
            logger.debug("add repo key {}, value {}, expireTime {}, timeUnit {}", key, value, expireTime, timeUnit);
        }
        return commands.setIfAbsent(nameSpace, key, value, expireTime, timeUnit);
    }

    @Override
    public boolean exist(String key) {
        if (logger.isDebugEnabled()) {
            logger.debug("is exist key {}", key);
        }
        return commands.get(nameSpace, key) != null;
    }

    @Override
    public IdempotentRecord get(String key) {
        String val = commands.get(nameSpace, key);
        if (val == null) {
            return null;
        } else {
            return new IdempotentRecord();
        }
    }

    @Override
    public boolean delete(String key) {
        commands.delete(nameSpace, key);
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        IdempotentConfig config = DynamicConfigLoader.load();
        String groupName = config.getGroupName();
        Assert.notNull(groupName, "idempotent.lock.group_name is required.");
        int namespace = config.getNamespace();
        this.nameSpace = namespace;
        this.commands = RedisFactory.getClusterValueCommands(groupName);
    }
}
