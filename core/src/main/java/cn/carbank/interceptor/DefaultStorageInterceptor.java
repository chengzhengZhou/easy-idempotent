package cn.carbank.interceptor;

import cn.carbank.IdempotentRequest;
import cn.carbank.MethodInterceptor;
import cn.carbank.constant.StorageType;
import cn.carbank.exception.MethodExecuteException;
import cn.carbank.repository.IdempotentRecordRepo;
import cn.carbank.repository.IdempotentRepoIntegrate;

import java.util.concurrent.ExecutorService;

/**
 * 存储拦截
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月15日
 */
public class DefaultStorageInterceptor implements MethodInterceptor {

    private final IdempotentRepoIntegrate repo;

    public DefaultStorageInterceptor(ExecutorService executorService) {
        repo = new IdempotentRepoIntegrate(executorService);
    }

    public void add(StorageType storeModule, IdempotentRecordRepo idempotentRecordRepo) {
        repo.addRepo(storeModule.name(), idempotentRecordRepo);
    }

    @Override
    public boolean preProcess(IdempotentRequest methodInfo) {
        // 幂等判断,不存在则放行
        return !repo.exist(methodInfo.getKey(), methodInfo.getStoreConfigList());
    }

    @Override
    public void postProcess(Object re, IdempotentRequest methodInfo) {
        // 缓存key
        repo.add(methodInfo.getKey(), "1", methodInfo.getStoreConfigList());
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
        System.out.println("异常缓存");
        repo.add(methodInfo.getKey(), "1", methodInfo.getStoreConfigList());
    }

}
