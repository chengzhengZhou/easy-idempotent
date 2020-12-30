package cn.carbank.idempotent;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 幂等请求信息
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月15日
 */
public class IdempotentRequest {
    /**
     * 锁名称
     */
    private String lockName;
    /**
     * 锁失效时间
     */
    private long expireTime;
    /**
     * 获取锁超时时间
     */
    private long tryTimeout;
    /**
     * 失效时间单位
     */
    private TimeUnit expireTimeUnit;
    /**
     * 幂等资源key
     */
    private String key;
    /**
     * 存储配置
     */
    private List<StorageConfig> storeConfigList;
    /**
     * 指定不缓存的异常类型
     */
    private Class<? extends Exception>[] unAcceptErrors;

    public List<StorageConfig> getStoreConfigList() {
        return storeConfigList;
    }

    public void setStoreConfigList(List<StorageConfig> storeConfigList) {
        this.storeConfigList = storeConfigList;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public TimeUnit getExpireTimeUnit() {
        return expireTimeUnit;
    }

    public void setExpireTimeUnit(TimeUnit expireTimeUnit) {
        this.expireTimeUnit = expireTimeUnit;
    }

    public Class<? extends Exception>[] getUnAcceptErrors() {
        return unAcceptErrors;
    }

    public void setUnAcceptErrors(Class<? extends Exception>[] unAcceptErrors) {
        this.unAcceptErrors = unAcceptErrors;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public long getTryTimeout() {
        return tryTimeout;
    }

    public void setTryTimeout(long tryTimeout) {
        this.tryTimeout = tryTimeout;
    }
}
