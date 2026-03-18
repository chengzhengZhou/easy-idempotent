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

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 幂等请求信息
 *
 * @author chengzhengZhou
 * @since 2020年12月15日
 */
public class IdempotentRequest {
    /**
     * 锁名称
     */
    private String lockName;
    /**
     * 锁失效时间
     */
    private long expireTime;
    /**
     * 获取锁超时时间
     */
    private long tryTimeout;
    /**
     * 失效时间单位
     */
    private TimeUnit expireTimeUnit;
    /**
     * 幂等资源key
     */
    private String key;
    /**
     * 存储配置
     */
    private List<StorageConfig> storeConfigList;
    /**
     * 指定不缓存的异常类型
     */
    private Class<? extends Exception>[] unAcceptErrors;

    public List<StorageConfig> getStoreConfigList() {
        return storeConfigList;
    }

    public void setStoreConfigList(List<StorageConfig> storeConfigList) {
        this.storeConfigList = storeConfigList;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public TimeUnit getExpireTimeUnit() {
        return expireTimeUnit;
    }

    public void setExpireTimeUnit(TimeUnit expireTimeUnit) {
        this.expireTimeUnit = expireTimeUnit;
    }

    public Class<? extends Exception>[] getUnAcceptErrors() {
        return unAcceptErrors;
    }

    public void setUnAcceptErrors(Class<? extends Exception>[] unAcceptErrors) {
        this.unAcceptErrors = unAcceptErrors;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public long getTryTimeout() {
        return tryTimeout;
    }

    public void setTryTimeout(long tryTimeout) {
        this.tryTimeout = tryTimeout;
    }
}
