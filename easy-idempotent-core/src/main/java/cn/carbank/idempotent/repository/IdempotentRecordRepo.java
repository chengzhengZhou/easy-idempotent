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

import java.util.concurrent.TimeUnit;

/**
 * 幂等记录存储读取
 *
 * @author chengzhengZhou
 * @since 2020年12月14日
 */
public interface IdempotentRecordRepo {

    /**
     * 添加记录
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     * @return
     */
    boolean add(String key, String value, int expireTime, TimeUnit timeUnit);

    /**
     * key对应的记录是否存在
     * @param key
     * @return
     */
    boolean exist(String key);

    /**
     * 根据key获取记录
     * 暂未使用到该方法
     *
     * @param key
     * @return
     */
    default IdempotentRecord get(String key){
        return null;
    }

    /**
     * 删除指定key的记录
     * 暂未使用到该方法
     *
     * @param key
     * @return
     */
    default boolean delete(String key) {
        return false;
    }

}
