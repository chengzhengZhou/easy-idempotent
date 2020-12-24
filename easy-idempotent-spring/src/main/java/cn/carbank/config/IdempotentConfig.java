package cn.carbank.config;

import cn.carbank.StorageConfig;

import java.util.List;

/**
 * 配置信息
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月16日
 */
public class IdempotentConfig {
    private String lockPre;
    /**
     * 核心线程数
     */
    private int core;
    /**
     * 最大线程数
     */
    private int max;
    /**
     * redis命名空间
     */
    private int namespace;
    /**
     * redis组名
     */
    private String groupName;
    /**
     * 全局配置的存储方式
     */
    private List<StorageConfig> storage;

    public IdempotentConfig() {
    }

    public List<StorageConfig> getStorage() {
        return storage;
    }

    public void setStorage(List<StorageConfig> storage) {
        this.storage = storage;
    }

    public int getCore() {
        return core;
    }

    public void setCore(int core) {
        this.core = core;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getNamespace() {
        return namespace;
    }

    public void setNamespace(int namespace) {
        this.namespace = namespace;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLockPre() {
        return lockPre;
    }

    public void setLockPre(String lockPre) {
        this.lockPre = lockPre;
    }

    @Override
    public String toString() {
        return "IdempotentConfig{" + "lockPre='" + lockPre + '\'' + ", core=" + core + ", max=" + max + ", namespace=" + namespace + ", groupName='" + groupName + '\'' + ", storage=" + storage + '}';
    }
}
