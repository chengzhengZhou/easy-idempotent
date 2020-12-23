package cn.carbank;

import cn.carbank.constant.StorageType;

import java.util.concurrent.TimeUnit;

/**
 * 存储配置
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public class StorageConfig {

    /**
     * 存储等级
     * 1-一级 2-二级 ...
     */
    private int level;
    /**
     * 超时时间
     */
    private int expireTime;
    /**
     * 时间单位
     */
    private TimeUnit timeUnit;
    /**
     * 存储类型
     */
    private StorageType storageModule;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public StorageType getStorageModule() {
        return storageModule;
    }

    public void setStorageModule(StorageType storageModule) {
        this.storageModule = storageModule;
    }

    @Override
    public String toString() {
        return "StorageConfig{" + "level=" + level + ", expireTime=" + expireTime + ", timeUnit=" + timeUnit + ", storeModule=" + storageModule + '}';
    }
}
