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
package cn.carbank.idempotent.repository;

import cn.carbank.idempotent.IdempotentRecord;
import cn.carbank.idempotent.constant.DateField;
import cn.carbank.idempotent.utils.DateUtil;
import cn.carbank.idempotent.utils.DelayedTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * 本地存储
 *
 * @author chengzhengZhou
 * @since 2020年12月14日
 */
public class MemoryIdempotentRecordRepoImpl implements IdempotentRecordRepo {
    private static final Logger logger = LoggerFactory.getLogger(MemoryIdempotentRecordRepoImpl.class);

    public static final Map<String, IdempotentRecord> RECORD_MAP = new ConcurrentHashMap<>();
    public static final DelayQueue<DelayedTask> DELAYED_TASKS = new DelayQueue<>();

    static {
        Thread t = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    DELAYED_TASKS.take().run();
                }
            } catch (InterruptedException e) {
                logger.error("consume delay tasks interrupted.", e);
            }
        });
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    @Override
    public boolean add(String key, String value, int expireTime, TimeUnit timeUnit) {
        if (logger.isDebugEnabled()) {
            logger.debug("memory add key:{} expireTime:{}", key, expireTime);
        }
        IdempotentRecord idempotentRecord = new IdempotentRecord();
        idempotentRecord.setKey(key);
        idempotentRecord.setValue(value);
        Date now = new Date();
        idempotentRecord.setAddTime(now);
        if (expireTime > 0) {
            Date offset = DateUtil.offset(now, DateField.of(timeUnit), expireTime);
            idempotentRecord.setExpireTime(offset);
        }
        RECORD_MAP.put(key, idempotentRecord);

        if (expireTime > 0) {
            DELAYED_TASKS.add(new CleanCacheTask(key, expireTime, timeUnit));
        }
        return true;
    }

    @Override
    public boolean exist(String key) {
        logger.debug("memory exist {}", key);
        return RECORD_MAP.containsKey(key);
    }

    @Override
    public IdempotentRecord get(String key) {
        logger.debug("memory get {}", key);
        return RECORD_MAP.get(key);
    }

    @Override
    public boolean delete(String key) {
        logger.debug("memory delete {}", key);
        RECORD_MAP.remove(key);
        return true;
    }

    static class CleanCacheTask extends DelayedTask {

        private String key;

        public CleanCacheTask(String key, long delay, TimeUnit timeUnit) {
            super(delay, timeUnit);
            this.key = key;
        }

        @Override
        public void run() {
            IdempotentRecord remove = RECORD_MAP.remove(key);
            if (logger.isDebugEnabled()) {
                logger.debug("remove record:{} by key:{}", remove, key);
            }
        }
    }
}
