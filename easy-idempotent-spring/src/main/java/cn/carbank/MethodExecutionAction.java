package cn.carbank;

import cn.carbank.exception.MethodExecuteException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 普通类方法执行
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
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
