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

import cn.carbank.idempotent.constant.StorageType;
import cn.carbank.idempotent.repository.IdempotentRecordRepo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 数据存储类获取
 *
 * @author chengzhengZhou
 * @since 2020年12月18日
 */
public class RecordRepositoryFactory {

    private static final RecordRepositoryFactory INSTANCE = new RecordRepositoryFactory();

    public RecordRepositoryFactory() {
    }

    public static RecordRepositoryFactory getInstance() {
        return INSTANCE;
    }

    protected volatile Map<StorageType, IdempotentRecordRepo> cache = null;

    public Map<StorageType, IdempotentRecordRepo> getRecordRepository() {
        if (cache != null) {
            return cache;
        }
        synchronized(RecordRepositoryFactory.class) {
            if (cache != null) {
                return cache;
            }
            ServiceLoader<IdempotentRecordRepo> repos = ServiceLoader.load(IdempotentRecordRepo.class);
            Iterator<IdempotentRecordRepo> iterator = repos.iterator();
            Map<StorageType, IdempotentRecordRepo> map = new HashMap<>();
            while(iterator.hasNext()) {
                IdempotentRecordRepo next = iterator.next();
                map.put(StorageType.ofName(next.getClass().getSimpleName()), next);
            }
            cache = map;
        }
        return cache;
    }

}
