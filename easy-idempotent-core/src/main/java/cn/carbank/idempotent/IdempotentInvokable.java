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

import cn.carbank.idempotent.exception.IdempotentBadRequestException;
import cn.carbank.idempotent.exception.IdempotentRuntimeException;
import cn.carbank.idempotent.exception.MethodExecuteException;

/**
 * 幂等接口
 *
 * @author chengzhengZhou
 * @since 2020年12月17日
 */
public interface IdempotentInvokable<R> {
    /**
     * 执行方法
     *
     * @return R
     *          执行结果
     * @throws IdempotentRuntimeException
     *          框架执行时异常
     * @throws IdempotentBadRequestException
     *          参数不合法时引发的异常
     * @throws MethodExecuteException
     *          方法执行异常
     */
    R execute();

}
