package cn.carbank.locksupport;

import cn.carbank.exception.IdempotentRuntimeException;
import cn.carbank.locksupport.Lock;
import cn.carbank.locksupport.LockClient;
import cn.carbank.locksupport.LockModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 请填写类注释
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月22日
 */
public class RedisLockClient implements LockClient {

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Lock getLock(String lock, LockModel lockModel) {
        Assert.notNull(lock, "lock key is required.");
        return new RedisLock(lock, stringRedisTemplate);
    }

    static class RedisLock implements Lock {
        private final Logger logger = LoggerFactory.getLogger(RedisLock.class);
        private StringRedisTemplate template;
        private String lock;
        private UUID uuid;

        public RedisLock(String lock, StringRedisTemplate template) {
            this.template = template;
            this.lock = lock;
            this.uuid = UUID.randomUUID();
        }

        public String getLockVal() {
            return String.format("%s:%s", uuid.toString(), Thread.currentThread().getId());
        }

        @Override
        public void lock(long timeout, TimeUnit timeUnit) {
            if (logger.isDebugEnabled()) {
                logger.debug("lock {}", lock);
            }
            // 均设置超时，等待两倍的过期时间
            int interruptedTimes = 0;
            boolean isLock = false;
            while(interruptedTimes < 10) {
                try {
                    isLock = tryLock(timeout, timeout * 2, timeUnit);
                    break;
                } catch (InterruptedException e) {
                    interruptedTimes++;
                }
            }
            if (!isLock) {
                throw new IdempotentRuntimeException("get lock " + lock + " fail.");
            }
        }

        @Override
        public boolean tryLock(long timeout, long tryTimeout, TimeUnit timeUnit) throws InterruptedException {
            if (logger.isDebugEnabled()) {
                logger.debug("try lock {}", lock);
            }
            boolean isLock = false;
            long time = timeUnit.toMillis(tryTimeout);
            long current = System.currentTimeMillis();
            long rest = 0;
            int shortRetryCount = 0;
            int retryCount = 0;
            for(;;) {
                isLock = template.opsForValue().setIfAbsent(lock, getLockVal(), timeout, timeUnit);
                if (isLock) {
                    return true;
                } else {
                    rest = time - (System.currentTimeMillis() - current);
                    if(rest < 0) {
                        return false;
                    }

                    String redisVal = template.opsForValue().get(lock);
                    if (redisVal == null) {
                        rest = time - (System.currentTimeMillis() - current);
                        shortRetryCount++;
                        retryCount++;

                        // 快速失败，可能是由于key的过期时间过短
                        if (rest < 0 || shortRetryCount >= 3) {
                            return false;
                        }
                        continue;
                    } else {
                        if (getLockVal().equals(redisVal)) {
                            return true;
                        } else {
                            rest = time - (System.currentTimeMillis() - current);
                            if (rest < 0) {
                                return false;
                            }
                            retryCount++;

                            autoSleep(retryCount, rest);
                        }
                    }
                }
            }
        }

        /**
         * 前5次快速尝试，后面固定折减，最长100毫秒，最短10毫秒
         *
         * @param retryCount
         * @param time 毫秒值
         */
        private void autoSleep(int retryCount, long time) throws InterruptedException {
            if (retryCount <= 5) {
                Thread.sleep(Math.min(time, (int) (Math.random() * 10)));
            } else {
                if (time < 10) {
                    Thread.sleep(10);
                } else {
                    long cutTime = (long) time >>> 1;
                    Thread.sleep(Math.min(100, Math.max(10, cutTime)));
                }
            }
        }

        @Override
        public boolean isLock() {
            if (logger.isDebugEnabled()) {
                logger.debug("is lock {}", lock);
            }
            String redisVal = template.opsForValue().get(lock);
            if (redisVal == null) {
                return false;
            }
            if (getLockVal().equals(redisVal)) {
                return true;
            }
            return false;
        }

        /**
         * 该解锁流程非完美方案
         * 极端情况出现误删非当前客户端设置的Key
         * @throws IllegalMonitorStateException
         *          锁超时或非当前线程设置的锁
         */
        @Override
        public void unlock() {
            if (logger.isDebugEnabled()) {
                logger.debug("unlock {}", lock);
            }
            if (isLock()) {
                template.delete(lock);
            } else {
                throw new IllegalMonitorStateException("attempt to unlock lock, not locked by current thread by id：" + getLockVal());
            }
        }
    }

}
