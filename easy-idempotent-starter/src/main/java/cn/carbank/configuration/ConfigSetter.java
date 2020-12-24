package cn.carbank.configuration;

import cn.carbank.StorageConfig;
import cn.carbank.config.IdempotentConfig;
import cn.carbank.constant.StorageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 配置赋值
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月24日
 */
public class ConfigSetter {
    private final Logger logger = LoggerFactory.getLogger(ConfigSetter.class);
    private IdempotentProperties properties;
    private IdempotentConfig idempotentConfig;

    ConfigSetter(IdempotentProperties properties) {
        this.properties = properties;
        this.idempotentConfig = new IdempotentConfig();
    }

    public static IdempotentConfig of(IdempotentProperties properties) {
        return (new ConfigSetter(properties)).build();
    }

    private IdempotentConfig build() {
        Assert.notNull(properties, "idempotent properties is required.");

        IdempotentProperties.LockProperties lockProperties = this.properties.getLock();
        String lockPre = Optional.ofNullable(lockProperties.getPre()).orElse("lock:");
        String groupName = lockProperties.getGroupName();
        Integer namespace = Optional.ofNullable(lockProperties.getNamespace()).orElse(1);

        int core = Optional.ofNullable(this.properties.getCore()).orElse(1);
        int max = Optional.ofNullable(this.properties.getMax()).orElse(10);

        String storage = this.properties.getStorage();
        List<StorageConfig> storageConfigList = new ArrayList<>();
        StorageConfig storageConfig = null;
        if (StringUtils.hasText(storage)) {
            String[] split = storage.split(",");
            for (int i = 0, j = 1; i < split.length; i++) {
                String item = split[i];
                if (StringUtils.hasText(item.trim())) {
                    StorageType storageType = StorageType.valueOf(item.toUpperCase());
                    switch(storageType) {
                        case MEMORY:
                            storageConfig = getStorageConfig(StorageType.MEMORY, this.properties.getMemory(), j);
                            break;
                        case REDIS:
                            storageConfig = getStorageConfig(StorageType.REDIS, this.properties.getRedis(), j);
                            break;
                        case MYSQL:
                            storageConfig = getStorageConfig(StorageType.MYSQL, this.properties.getMysql(), j);
                            break;
                        case MANGO:
                            storageConfig = getStorageConfig(StorageType.MANGO, this.properties.getMango(), j);
                            break;
                        case EHCACHE:
                            storageConfig = getStorageConfig(StorageType.EHCACHE, this.properties.getEhcache(), j);
                            break;
                        default:
                            break;
                    }
                    if (storageConfig != null) {
                        storageConfigList.add(storageConfig);
                        j++;
                    }
                }
            }
        }
        if (storageConfigList.size() <= 0) {
            storageConfig = new StorageConfig();
            storageConfig.setLevel(1);
            storageConfig.setExpireTime(1);
            storageConfig.setTimeUnit(TimeUnit.MINUTES);
            storageConfig.setStorageModule(StorageType.REDIS);
            storageConfigList.add(storageConfig);
        }

        idempotentConfig.setLockPre(lockPre);
        idempotentConfig.setGroupName(groupName);
        idempotentConfig.setNamespace(namespace);
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        idempotentConfig.setCore(Math.min(core, availableProcessors));
        idempotentConfig.setMax(max);
        idempotentConfig.setStorage(storageConfigList);
        logger.info("load idempotent config success {}", idempotentConfig);
        return this.idempotentConfig;
    }

    private StorageConfig getStorageConfig(StorageType type, IdempotentProperties.StorageProperties prop, int order) {
        StorageConfig storageConfig = new StorageConfig();
        String pre = String.format("idempotent.storage.%s.", type.name().toLowerCase());
        Assert.notNull(prop.getExpireTime(), pre + "expire_time is required.");
        Assert.notNull(prop.getTimeUnit(), pre + "time_unit is required.");
        storageConfig.setStorageModule(type);
        storageConfig.setLevel(order);
        storageConfig.setExpireTime(prop.getExpireTime());
        storageConfig.setTimeUnit(prop.getTimeUnit());
        return storageConfig;
    }
}
