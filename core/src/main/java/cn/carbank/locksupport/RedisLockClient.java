package cn.carbank.locksupport;

/**
 * redis锁客户端
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public class RedisLockClient implements LockClient {
    /**
     * 获取锁对象
     * @param lockModel
     * @return
     */
    public Lock getLock(String lockName, LockModel lockModel) {
        return new RedisLock(lockName);
    }
}
