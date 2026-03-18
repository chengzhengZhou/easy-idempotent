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

import cn.carbank.idempotent.exception.MethodExecuteException;
import cn.carbank.idempotent.utils.Exceptions;

import java.util.concurrent.ExecutorService;

/**
 * 继承该基类可实现通用幂等逻辑
 *
 * @author chengzhengZhou
 * @since 2020年12月17日
 */
public abstract class IdempotentCommand<R> extends AbstractCommand<R> implements IdempotentInvokable<R> {

    public IdempotentCommand(IdempotentRequest idempotentRequest) {
        super(idempotentRequest);
    }

    public IdempotentCommand(IdempotentRequest idempotentRequest, ExecutorService executorService) {
        super(idempotentRequest, executorService);
    }

    public IdempotentCommand(IdempotentRequest idempotentRequest, ExecutorService executorService, LockClientFactory lockClientFactory, RecordRepositoryFactory recordRepositoryFactory) {
        super(idempotentRequest, executorService, lockClientFactory, recordRepositoryFactory);
    }

    protected IdempotentCommand(IdempotentRequest idempotentRequest, LockClientFactory lockClientFactory, MethodInterceptor methodInterceptor) {
        super(idempotentRequest, lockClientFactory, methodInterceptor);
    }

    public IdempotentCommand(Setter setter) {
        super(setter);
    }

    @Override
    public R execute() {
        return synExecute();
    }

    @Override
    protected final Action getExecution() {
        return new Action() {
            @Override
            Object execute() throws MethodExecuteException {
                try {
                    return run();
                } catch (Exception e) {
                    throw Exceptions.sneakyThrow(decomposeException(e));
                }
            }
        };
    }

    @Override
    protected final Action getIdempotent() {
        return new Action() {
            @Override
            Object execute() throws MethodExecuteException {
                return getIdempotentResult();
            }
        };
    }

    /**
     * 实现该方法将在执行execute后被执行
     *
     * @return R
     * @throws Exception
     */
    protected abstract R run() throws Exception;

    /**
     * 当被判定为幂等请求后会被调用
     *
     * @return R
     * @throws UnsupportedOperationException
     *          如果未实现该方法，则抛出异常
     */
    protected R getIdempotentResult() {
        throw new UnsupportedOperationException("No idempotent method available.");
    }

}
