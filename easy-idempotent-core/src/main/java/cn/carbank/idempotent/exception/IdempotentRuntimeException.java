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
package cn.carbank.idempotent.exception;

/**
 * 执行异常
 *
 * @author chengzhengZhou
 * @since 2020年12月17日
 */
public class IdempotentRuntimeException extends RuntimeException {

    private Throwable error;

    public IdempotentRuntimeException(String message) {
        super(message);
    }

    public IdempotentRuntimeException(String message, Throwable error) {
        super(message);
        this.error = error;
    }

    public IdempotentRuntimeException(Throwable cause) {
        super(cause);
    }

    public Throwable getError() {
        return error;
    }
}
