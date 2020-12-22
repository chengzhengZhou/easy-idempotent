package cn.carbank.constant;

/**
 * 存储模块
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public enum StorageType {

    REDIS,MYSQL,MANGO,MEMORY;

    public static StorageType ofName(String beanName) {
        if (beanName == null) {
            return null;
        }
        beanName = beanName.toLowerCase();
        if (beanName.startsWith("redis")) {
            return REDIS;
        } else if (beanName.startsWith("mysql")) {
            return MYSQL;
        } else if (beanName.startsWith("mango")) {
            return MANGO;
        } else if (beanName.startsWith("memory")) {
            return MEMORY;
        } else {
            // not support
            return null;
        }
    }

}
