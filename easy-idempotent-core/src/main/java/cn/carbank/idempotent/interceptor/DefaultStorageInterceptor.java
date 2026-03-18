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
package cn.carbank.idempotent.interceptor;

import cn.carbank.idempotent.IdempotentRequest;
import cn.carbank.idempotent.MethodInterceptor;
import cn.carbank.idempotent.constant.StorageType;
import cn.carbank.idempotent.exception.MethodExecuteException;
import cn.carbank.idempotent.repository.IdempotentRecordRepo;
import cn.carbank.idempotent.repository.IdempotentRepoComposite;

import java.util.concurrent.ExecutorService;

/**
 * 存储拦截
 *
 * @author chengzhengZhou
 * @since 2020年12月15日
 */
public class DefaultStorageInterceptor implements MethodInterceptor {

    private static final String DEFAULT_VAL = "1";
    private final IdempotentRepoComposite repo;

    public DefaultStorageInterceptor(ExecutorService executorService) {
        repo = new IdempotentRepoComposite(executorService);
    }

    public void add(StorageType storeModule, IdempotentRecordRepo idempotentRecordRepo) {
        repo.addRepo(storeModule.name(), idempotentRecordRepo);
    }

    @Override
    public boolean preProcess(IdempotentRequest methodInfo) {
        return !repo.exist(methodInfo.getKey(), methodInfo.getStoreConfigList());
    }

    @Override
    public void postProcess(Object re, IdempotentRequest methodInfo) {
        repo.add(methodInfo.getKey(), DEFAULT_VAL, methodInfo.getStoreConfigList());
    }

    @Override
    public void errorProcess(Exception exception, IdempotentRequest methodInfo) {
        Throwable throwable = exception;
        if (throwable instanceof MethodExecuteException) {
            throwable = exception.getCause();
        }
        // 若为指定异常则不存储，否则仍缓存key
        if (methodInfo.getUnAcceptErrors() != null) {
            for (Class item : methodInfo.getUnAcceptErrors()) {
                if (item.isAssignableFrom(throwable.getClass())) {
                    return;
                }
            }
        }
        repo.add(methodInfo.getKey(), DEFAULT_VAL, methodInfo.getStoreConfigList());
    }

}
