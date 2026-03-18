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

import java.util.concurrent.TimeUnit;

/**
 * 存储配置
 *
 * @author chengzhengZhou
 * @since 2020年12月14日
 */
public class StorageConfig {

    /**
     * 存储等级
     * 1-一级 2-二级 ...
     */
    private int level;
    /**
     * 超时时间
     */
    private int expireTime;
    /**
     * 时间单位
     */
    private TimeUnit timeUnit;
    /**
     * 存储类型
     */
    private StorageType storageModule;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public StorageType getStorageModule() {
        return storageModule;
    }

    public void setStorageModule(StorageType storageModule) {
        this.storageModule = storageModule;
    }

    @Override
    public String toString() {
        return "StorageConfig{" + "level=" + level + ", expireTime=" + expireTime + ", timeUnit=" + timeUnit + ", storeModule=" + storageModule + '}';
    }
}
