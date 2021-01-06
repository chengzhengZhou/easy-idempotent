package cn.carbank.idempotent.locksupport;

import java.util.HashMap;
import java.util.Map;

/**
 * 锁客户端
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public class DefaultLockClient implements LockClient {
    public static final ThreadLocal<Map<String, DefaultLock>> LOCAL_LOCK = new ThreadLocal<>();
    /**
     * 获取锁对象
     * @param lockModel
     * @return
     */
    public Lock getLock(String lockName, LockModel lockModel) {
        if (lockName == null) {
            throw new IllegalArgumentException("lock key is required");
        }
        Map<String, DefaultLock> lockMap = LOCAL_LOCK.get();
        if (lockMap == null) {
            lockMap = new HashMap<>();
            LOCAL_LOCK.set(lockMap);
        }
        if (!lockMap.containsKey(lockName)) {
            lockMap.put(lockName, new DefaultLock(lockName));
        }

        return lockMap.get(lockName);
    }
}
