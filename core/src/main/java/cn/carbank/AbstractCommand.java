package cn.carbank;

import cn.carbank.constant.StorageType;
import cn.carbank.exception.IdempotentBadRequestException;
import cn.carbank.exception.IdempotentRuntimeException;
import cn.carbank.exception.MethodExecuteException;
import cn.carbank.interceptor.DefaultStorageInterceptor;
import cn.carbank.locksupport.Lock;
import cn.carbank.locksupport.LockClient;
import cn.carbank.locksupport.LockModel;
import cn.carbank.repository.IdempotentRecordRepo;
import cn.carbank.utils.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 幂等流程实现
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月17日
 */
abstract class AbstractCommand<R> {
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractCommand.class);

    private final LockClient lockClient;

    private final MethodInterceptor interceptor;

    private final IdempotentRequest idempotentRequest;

    private ExecutorService executorService;

    AbstractCommand(IdempotentRequest idempotentRequest) {
        this.idempotentRequest = idempotentRequest;
        this.lockClient = LockClientFactory.getInstance().getClient();
        this.interceptor = initInterceptor(RecordRepositoryFactory.getInstance());
    }

    public AbstractCommand(Setter setter) {
        this(setter.getIdempotentRequest());
    }

    AbstractCommand(IdempotentRequest idempotentRequest, ExecutorService executorService) {
        this.idempotentRequest = idempotentRequest;
        this.executorService = executorService;
        this.lockClient = LockClientFactory.getInstance().getClient();
        this.interceptor = initInterceptor(RecordRepositoryFactory.getInstance());
    }

    AbstractCommand(IdempotentRequest idempotentRequest, ExecutorService executorService,
                    LockClientFactory lockClientFactory, RecordRepositoryFactory recordRepositoryFactory) {
        this.idempotentRequest = idempotentRequest;
        this.executorService = executorService;
        this.lockClient = lockClientFactory.getClient();
        this.interceptor = initInterceptor(recordRepositoryFactory);
    }

    private MethodInterceptor initInterceptor(RecordRepositoryFactory recordRepositoryFactory) {
        Map<StorageType, IdempotentRecordRepo> map = recordRepositoryFactory.getRecordRepository();
        DefaultStorageInterceptor storageInterceptor = new DefaultStorageInterceptor(executorService);
        map.forEach((k, v) -> storageInterceptor.add(k, v));
        return storageInterceptor;
    }

    R synExecute() {
        valid();
        Lock lock = lockClient.getLock(idempotentRequest.getLockName(), LockModel.REENTRANT);
        boolean isLocked = false;
        if (idempotentRequest.getTryTimeout() > 0) {
            try {
                isLocked = lock.tryLock(idempotentRequest.getExpireTime(), idempotentRequest.getTryTimeout(), idempotentRequest.getExpireTimeUnit());
            } catch (InterruptedException e) {
                logger.error("get lock {} interrupted.", idempotentRequest.getLockName(), e);
                throw new IdempotentRuntimeException("lock time out.", e);
            }
            if (!isLocked) {
                throw new IdempotentRuntimeException("get lock " + idempotentRequest.getLockName() + " time out.");
            }
        }
        try {
            if (!isLocked) {
                lock.lock(idempotentRequest.getExpireTime(), idempotentRequest.getExpireTimeUnit());
            }
            boolean idempotentReq = interceptor.preProcess(idempotentRequest);
            if (!idempotentReq) {
                return (R) getIdempotent().execute();
            }

            R result = null;
            Exception error = null;
            try {
                result = (R) getExecution().execute();
                interceptor.postProcess(result, idempotentRequest);
            } catch (Exception e) {
                error = e;
                interceptor.errorProcess(e, idempotentRequest);
            }

            if (error != null) {
                throw Exceptions.sneakyThrow(decomposeException(error));
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    protected void valid() {
        if (idempotentRequest == null) {
            throw new IdempotentBadRequestException("IdempotentRequest is required.");
        }
        if (interceptor == null) {
            throw new IdempotentBadRequestException("MethodInterceptor is required");
        }
    }

    protected abstract Action getExecution();

    protected abstract Action getIdempotent();

    protected Throwable decomposeException(Exception e) {
        if (e instanceof IllegalStateException) {
            return (IllegalStateException) e;
        }
        if (e instanceof IdempotentBadRequestException) {
            return (IdempotentBadRequestException) e;
        }
        if (e.getCause() instanceof IdempotentBadRequestException) {
            return (IdempotentBadRequestException) e.getCause();
        }
        if (e instanceof IdempotentRuntimeException) {
            return (IdempotentRuntimeException) e;
        }
        if (e.getCause() instanceof IdempotentRuntimeException) {
            return (IdempotentRuntimeException) e.getCause();
        }
        if (e instanceof MethodExecuteException) {
            return (MethodExecuteException) e;
        }
        if (e.getCause() instanceof MethodExecuteException) {
            return (MethodExecuteException) e;
        }
        return new MethodExecuteException(e);
    }

    abstract class Action {
        /**
         * 执行方法包装
         *
         * @return execution result
         * @throws MethodExecuteException
         */
        abstract Object execute() throws MethodExecuteException;
    }

    final public static class Setter {

        private IdempotentRequest idempotentRequest;

        public Setter(IdempotentRequest idempotentRequest) {
            this.idempotentRequest = idempotentRequest;
        }

        public static Setter withLock(String lockName) {
            IdempotentRequest idempotentRequest = new IdempotentRequest();
            idempotentRequest.setLockName(lockName);
            return new Setter(idempotentRequest);
        }

        public Setter expireTime(long expireTime) {
            this.idempotentRequest.setExpireTime(expireTime);
            return this;
        }

        public Setter tryTimeout(long tryTimeout) {
            this.idempotentRequest.setTryTimeout(tryTimeout);
            return this;
        }

        public Setter expireTimeUnit(TimeUnit timeUnit) {
            this.idempotentRequest.setExpireTimeUnit(timeUnit);
            return this;
        }

        public Setter noStorageException(Class<? extends Exception>[] classes) {
            this.idempotentRequest.setUnAcceptErrors(classes);
            return this;
        }

        public Setter key(String key) {
            this.idempotentRequest.setKey(key);
            return this;
        }

        public Setter storageConfig(int level, StorageType storageModule, int expireTime, TimeUnit timeUnit) {
            List<StorageConfig> storeConfigList = this.idempotentRequest.getStoreConfigList();
            if (storeConfigList == null) {
                storeConfigList = new ArrayList<>();
            }
            StorageConfig config = new StorageConfig();
            config.setLevel(level);
            config.setStorageModule(storageModule);
            config.setExpireTime(expireTime);
            config.setTimeUnit(timeUnit);
            storeConfigList.add(config);
            this.idempotentRequest.setStoreConfigList(storeConfigList);
            return this;
        }

        public IdempotentRequest getIdempotentRequest() {
            return idempotentRequest;
        }
    }

}
