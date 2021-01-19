package cn.carbank.idempotent;

import cn.carbank.idempotent.exception.MethodExecuteException;
import cn.carbank.idempotent.utils.Exceptions;

import java.util.concurrent.ExecutorService;

/**
 * 继承该基类可实现通用幂等逻辑
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
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
