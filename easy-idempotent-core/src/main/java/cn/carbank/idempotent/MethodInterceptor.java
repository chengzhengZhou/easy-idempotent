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

/**
 * 拦截通知
 *
 * @author chengzhengZhou
 * @since 2020年12月15日
 */
public interface MethodInterceptor {

    default boolean preProcess(IdempotentRequest methodInfo) {
        return true;
    }

    default void postProcess(Object re, IdempotentRequest methodInfo) {

    }

    default void errorProcess(Exception exception, IdempotentRequest methodInfo) {

    }
}
