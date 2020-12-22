package cn.carbank.locksupport;

import java.util.concurrent.TimeUnit;

/**
 * 锁门面
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public interface Lock {
    /**
     * 上锁
     *
     * @param timeout 锁失效时间
     * @param timeUnit 时间单位
     * @return void
     */
    void lock(long timeout, TimeUnit timeUnit);

    /**
     * 尝试上锁，直到等待超时
     *
     * @param timeout 锁失效时间
     * @param tryTimeout 锁尝试超时
     * @param timeUnit 时间单位
     * @return boolean
     * @throws InterruptedException
     */
    boolean tryLock(long timeout, long tryTimeout, TimeUnit timeUnit) throws InterruptedException;

    /**
     * 判断是否上锁
     *
     * @return boolean
     */
    boolean isLock();

    /**
     * 解锁
     * @return void
     */
    void unlock();
}
