package cn.carbank.idempotent.locksupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * redis锁
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public class DefaultLock implements Lock {

    public static final ConcurrentHashMap<String, ReentrantLock> LOCK_MAP = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(DefaultLock.class);

    private String lockName;

    private ReentrantLock reentrantLock;

    public DefaultLock(String lockName) {
        this.lockName = lockName;
        ReentrantLock lock = new ReentrantLock();
        ReentrantLock originLock = LOCK_MAP.putIfAbsent(lockName, lock);
        if (originLock == null) {
            this.reentrantLock = lock;
        } else {
            this.reentrantLock = originLock;
        }
    }

    @Override
    public void lock(long timeout, TimeUnit timeUnit) {
        if (logger.isDebugEnabled()) {
            logger.debug("DefaultLock#lock {}", lockName);
        }
        reentrantLock.lock();
    }

    @Override
    public boolean tryLock(long timeout, long tryTimeout, TimeUnit timeUnit) throws InterruptedException {
        if (logger.isDebugEnabled()) {
            logger.debug("DefaultLock#tryLock {}", lockName);
        }
        return reentrantLock.tryLock(tryTimeout, timeUnit);
    }

    @Override
    public boolean isLock() {
        if (logger.isDebugEnabled()) {
            logger.debug("DefaultLock#isLock {}", lockName);
        }
        return reentrantLock.isLocked();
    }

    @Override
    public void unlock() {
        if (logger.isDebugEnabled()) {
            logger.debug("DefaultLock#unlock {}", lockName);
        }
        reentrantLock.unlock();
        ReentrantLock remove = LOCK_MAP.remove(lockName);
    }
}
