package cn.carbank.idempotent.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * 配置
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月23日
 */
@ConfigurationProperties(prefix = "idempotent")
public class IdempotentProperties {

    private Boolean enable;

    private Integer core;

    private Integer max;

    private String storage;

    private LockProperties lock = new LockProperties();

    private StorageProperties redis = new StorageProperties();

    private StorageProperties ehcache = new StorageProperties();

    private StorageProperties mysql = new StorageProperties();

    private StorageProperties mango = new StorageProperties();

    private StorageProperties memory = new StorageProperties();

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getCore() {
        return core;
    }

    public void setCore(Integer core) {
        this.core = core;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public LockProperties getLock() {
        return lock;
    }

    public void setLock(LockProperties lock) {
        this.lock = lock;
    }

    public StorageProperties getRedis() {
        return redis;
    }

    public void setRedis(StorageProperties redis) {
        this.redis = redis;
    }

    public StorageProperties getEhcache() {
        return ehcache;
    }

    public void setEhcache(StorageProperties ehcache) {
        this.ehcache = ehcache;
    }

    public StorageProperties getMysql() {
        return mysql;
    }

    public void setMysql(StorageProperties mysql) {
        this.mysql = mysql;
    }

    public StorageProperties getMango() {
        return mango;
    }

    public void setMango(StorageProperties mango) {
        this.mango = mango;
    }

    public StorageProperties getMemory() {
        return memory;
    }

    public void setMemory(StorageProperties memory) {
        this.memory = memory;
    }

    class StorageProperties {
        private Integer expireTime;

        private TimeUnit timeUnit;

        public Integer getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Integer expireTime) {
            this.expireTime = expireTime;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        public void setTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
        }
    }

    class LockProperties {
        private String pre;

        private String groupName;

        private Integer namespace;

        public String getPre() {
            return pre;
        }

        public void setPre(String pre) {
            this.pre = pre;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public Integer getNamespace() {
            return namespace;
        }

        public void setNamespace(Integer namespace) {
            this.namespace = namespace;
        }
    }
}
