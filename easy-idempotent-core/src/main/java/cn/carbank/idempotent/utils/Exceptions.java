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
package cn.carbank.idempotent.utils;

/**
 * 异常处理
 *
 * @author chengzhengZhou
 * @since 2020年12月18日
 */
public class Exceptions {

    /**
     * 将异常装换为运行时异常，以便代码中不用抛出
     *
     * @param t
     * @return
     */
    public static RuntimeException sneakyThrow(Throwable t) {
        return Exceptions.<RuntimeException>doThrow(t);
    }

    private static <T extends Throwable> T doThrow(Throwable ex) throws T {
        throw (T) ex;
    }
}
