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

import java.util.HashMap;
import java.util.Map;

/**
 * 锁客户端
 *
 * @author chengzhengZhou
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
