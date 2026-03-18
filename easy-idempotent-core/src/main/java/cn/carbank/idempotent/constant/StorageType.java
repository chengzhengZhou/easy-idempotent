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
package cn.carbank.idempotent.constant;

/**
 * 存储模块
 *
 * @author chengzhengZhou
 * @since 2020年12月14日
 */
public enum StorageType {

    REDIS,EHCACHE,MYSQL,MANGO,MEMORY;

    public static StorageType ofName(String beanName) {
        if (beanName == null) {
            return null;
        }
        beanName = beanName.toLowerCase();
        if (beanName.startsWith("redis")) {
            return REDIS;
        } else if (beanName.startsWith("mysql")) {
            return MYSQL;
        } else if (beanName.startsWith("mango")) {
            return MANGO;
        } else if (beanName.startsWith("memory")) {
            return MEMORY;
        } else if (beanName.startsWith("ehcache")) {
            return EHCACHE;
        } else {
            // not support
            return null;
        }
    }

}
