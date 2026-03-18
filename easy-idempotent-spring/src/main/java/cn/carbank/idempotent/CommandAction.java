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

/**
 * 方法包装，为了方便调用
 *
 * @author chengzhengZhou
 * @since 2020年12月18日
 */
public interface CommandAction {
    /**
     * 执行方法
     *
     * @return
     * @throws MethodExecuteException
     */
    Object execute() throws MethodExecuteException;

    /**
     * 传参执行
     *
     * @param args  方法参数
     * @return
     * @throws MethodExecuteException
     */
    Object executeWithArgs(Object[] args) throws MethodExecuteException;
}
