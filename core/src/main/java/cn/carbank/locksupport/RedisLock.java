package cn.carbank.locksupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * redis锁
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public class RedisLock implements Lock {

    private final Logger logger = LoggerFactory.getLogger(RedisLock.class);

    private String key;

    boolean isLock = false;

    private Thread lockThread;

    public RedisLock(String key) {
        this.key = key;
    }

    @Override
    public void lock(long timeout, TimeUnit timeUnit) {
        logger.debug("{} 上锁", key);
        isLock = true;
        lockThread = Thread.currentThread();
    }

    @Override
    public boolean tryLock(long timeout, long tryTimeout, TimeUnit timeUnit) throws InterruptedException {
        logger.debug("带超时的上锁");
        isLock = true;
        lockThread = Thread.currentThread();
        return true;
    }

    @Override
    public boolean isLock() {
        logger.debug("是否上锁");
        return isLock;
    }

    @Override
    public void unlock() {
        logger.debug("{} 解锁", key);
        isLock = false;
        lockThread = null;
    }
}
