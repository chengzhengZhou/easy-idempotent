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
package cn.carbank.idempotent;

import cn.carbank.idempotent.locksupport.LockClient;

import java.util.ServiceLoader;

/**
 * 锁客户端工厂
 *
 * @author chengzhengZhou
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
