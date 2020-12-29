package cn.carbank.interceptor;

import cn.carbank.IdempotentRequest;
import cn.carbank.MethodInterceptor;
import cn.carbank.constant.StorageType;
import cn.carbank.exception.MethodExecuteException;
import cn.carbank.repository.IdempotentRecordRepo;
import cn.carbank.repository.IdempotentRepoComposite;

import java.util.concurrent.ExecutorService;

/**
 * 存储拦截
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月15日
 */
public class DefaultStorageInterceptor implements MethodInterceptor {

    private static final String DEFAULT_VAL = "1";
    private final IdempotentRepoComposite repo;

    public DefaultStorageInterceptor(ExecutorService executorService) {
        repo = new IdempotentRepoComposite(executorService);
    }

    public void add(StorageType storeModule, IdempotentRecordRepo idempotentRecordRepo) {
        repo.addRepo(storeModule.name(), idempotentRecordRepo);
    }

    @Override
    public boolean preProcess(IdempotentRequest methodInfo) {
        return !repo.exist(methodInfo.getKey(), methodInfo.getStoreConfigList());
    }

    @Override
    public void postProcess(Object re, IdempotentRequest methodInfo) {
        repo.add(methodInfo.getKey(), DEFAULT_VAL, methodInfo.getStoreConfigList());
    }

    @Override
    public void errorProcess(Exception exception, IdempotentRequest methodInfo) {
        Throwable throwable = exception;
        if (throwable instanceof MethodExecuteException) {
            throwable = exception.getCause();
        }
        // 若为指定异常则不存储，否则仍缓存key
        if (methodInfo.getUnAcceptErrors() != null) {
            for (Class item : methodInfo.getUnAcceptErrors()) {
                if (item.isAssignableFrom(throwable.getClass())) {
                    return;
                }
            }
        }
        repo.add(methodInfo.getKey(), DEFAULT_VAL, methodInfo.getStoreConfigList());
    }

}
