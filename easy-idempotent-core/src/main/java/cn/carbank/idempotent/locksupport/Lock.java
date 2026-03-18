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

import java.util.concurrent.TimeUnit;

/**
 * 锁门面
 *
 * @author chengzhengZhou
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
