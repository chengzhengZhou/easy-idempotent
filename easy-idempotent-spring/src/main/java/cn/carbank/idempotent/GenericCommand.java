package cn.carbank.idempotent;

import java.util.concurrent.ExecutorService;

/**
 * 通过注解的方法被解析后包装幂等实现类而被执行
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月18日
 */
public class GenericCommand extends IdempotentCommand<Object> {

    private CommandAction executionAction;

    private CommandAction idempotentAction;

    GenericCommand(CommandAction executionAction, CommandAction idempotentAction,
                   IdempotentRequest idempotentRequest,
                   ExecutorService executorService,
                   LockClientFactory lockClientFactory,
                   RecordRepositoryFactory recordRepositoryFactory) {
        super(idempotentRequest, executorService, lockClientFactory, recordRepositoryFactory);
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
