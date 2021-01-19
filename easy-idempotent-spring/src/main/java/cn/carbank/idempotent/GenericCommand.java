package cn.carbank.idempotent;

/**
 * 通过注解的方法被解析后包装幂等实现类而被执行
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月18日
 */
public class GenericCommand extends IdempotentCommand<Object> {

    private CommandAction executionAction;

    private CommandAction idempotentAction;

    public GenericCommand(CommandAction executionAction, CommandAction idempotentAction,
                          IdempotentRequest idempotentRequest,
                          LockClientFactory lockClientFactory,
                          MethodInterceptor methodInterceptor) {
        super(idempotentRequest, lockClientFactory, methodInterceptor);
        this.executionAction = executionAction;
        this.idempotentAction = idempotentAction;
    }

    @Override
    protected Object run() throws Exception {
        return executionAction.execute();
    }

    @Override
    protected Object getIdempotentResult() {
        return idempotentAction.execute();
    }
}
