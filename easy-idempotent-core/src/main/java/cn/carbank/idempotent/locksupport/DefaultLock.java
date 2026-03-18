/*
 * Copyright 2020 chengzhengZhou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.carbank.idempotent.locksupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 单机锁，由于增加计数器，仅限单线程
 *
 * @author chengzhengZhou
 * @since 2020年12月14日
 */
public class DefaultLock implements Lock {

    public static final ConcurrentHashMap<String, ReentrantLock> LOCK_MAP = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(DefaultLock.class);

    private String lockName;

    private ReentrantLock reentrantLock;

    private int monitor;

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
        monitor++;
    }

    @Override
    public boolean tryLock(long timeout, long tryTimeout, TimeUnit timeUnit) throws InterruptedException {
        if (logger.isDebugEnabled()) {
            logger.debug("DefaultLock#tryLock {}", lockName);
        }
        boolean locked = reentrantLock.tryLock(tryTimeout, timeUnit);
        if (locked) {
            monitor++;
        }
        return locked;
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
        monitor--;
        if (monitor <= 0) {
            DefaultLockClient.LOCAL_LOCK.remove();
        }
        LOCK_MAP.remove(lockName);

        reentrantLock.unlock();
    }
}
