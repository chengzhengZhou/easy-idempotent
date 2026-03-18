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
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 普通类方法执行
 *
 * @author chengzhengZhou
 * @since 2020年12月18日
 */
public class MethodExecutionAction implements CommandAction {

    private final Object object;
    private final Method method;
    private final Object[] _args;

    public MethodExecutionAction(Object object, Method method, Object[] args) {
        this.object = object;
        this.method = method;
        this._args = args;
    }

    @Override
    public Object execute() throws MethodExecuteException {
        try {
            ReflectionUtils.makeAccessible(method);
            return method.invoke(object, _args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MethodExecuteException(e);
        }
    }

    @Override
    public Object executeWithArgs(Object[] args) throws MethodExecuteException {
        try {
            ReflectionUtils.makeAccessible(method);
            return method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MethodExecuteException(e);
        }
    }

}
