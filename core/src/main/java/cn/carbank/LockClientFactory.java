package cn.carbank;

import cn.carbank.locksupport.LockClient;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * 锁客户端工厂
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public class LockClientFactory {


    private static final LockClientFactory INSTANCE = new LockClientFactory();

    public LockClientFactory() {
    }

    public static LockClientFactory getInstance() {
        return INSTANCE;
    }

    protected volatile LockClient lockClient;
    /**
     * 获取锁客户端
     * 默认返回第一个
     *
     * @return
     */
    public LockClient getClient() {
        if (lockClient != null) {
            return lockClient;
        }
        synchronized(LockClient.class) {
            if (lockClient != null) {
                return lockClient;
            }
            ServiceLoader<LockClient> lockClients = ServiceLoader.load(LockClient.class);
            lockClient = lockClients.iterator().next();
        }
        return lockClient;
    }

}
