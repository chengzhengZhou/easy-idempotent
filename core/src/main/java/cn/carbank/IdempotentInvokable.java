package cn.carbank;

import cn.carbank.exception.IdempotentBadRequestException;
import cn.carbank.exception.IdempotentRuntimeException;
import cn.carbank.exception.MethodExecuteException;

/**
 * 幂等接口
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
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
