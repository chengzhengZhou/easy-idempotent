package cn.carbank.idempotent;

import cn.carbank.idempotent.annotation.Idempotent;
import cn.carbank.idempotent.annotation.StorageParam;
import cn.carbank.idempotent.config.IdempotentConfig;
import cn.carbank.idempotent.exception.MethodExecuteException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 解析元数据生成可调用的类
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月18日
 */
public class IdempotentInvokableFactory {

    private static final IdempotentInvokableFactory INSTANCE = new IdempotentInvokableFactory();
    private ExecutorService executorService;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final SpringLockClientFactory lockClientFactory = new SpringLockClientFactory();
    private final SpringRecordRepositoryFactory recordRepositoryFactory = new SpringRecordRepositoryFactory();
    public static IdempotentInvokableFactory getInstance() {
        return INSTANCE;
    }

    public IdempotentInvokable create(MetaHolder metaHolder, MethodInvocation invocation, IdempotentConfig config, ApplicationContext applicationContext) {
        IdempotentRequest idempotentRequest = buildRequest(metaHolder.getIdempotent(), metaHolder, invocation.getArguments(), config);
        CommandAction execution = new InnerCommandAction(invocation);
        CommandAction idempotentAct = new MethodExecutionAction(metaHolder.getBean(), metaHolder.getIdempotentMethod(), invocation.getArguments());

        LockClientFactory lockClientFactory = getLockClientFactory(applicationContext);
        RecordRepositoryFactory recordRepositoryFactory = getRecordRepositoryFactory(applicationContext);

        ExecutorService executor = getExecutor(config);

        return new GenericCommand(execution, idempotentAct, idempotentRequest, executor, lockClientFactory, recordRepositoryFactory);
    }

    private LockClientFactory getLockClientFactory(ApplicationContext applicationContext) {
        if (this.lockClientFactory.getApplicationContext() == null) {
            this.lockClientFactory.setApplicationContext(applicationContext);
        }
        return this.lockClientFactory;
    }

    private RecordRepositoryFactory getRecordRepositoryFactory(ApplicationContext applicationContext) {
        if (this.recordRepositoryFactory.getApplicationContext() == null) {
            this.recordRepositoryFactory.setApplicationContext(applicationContext);
        }
        return this.recordRepositoryFactory;
    }

    private ExecutorService getExecutor(IdempotentConfig config) {
        if (this.executorService != null) {
            return this.executorService;
        }
        if (config.getStorage() == null || config.getStorage().size() <= 1) {
            return null;
        }
        synchronized(INSTANCE) {
            if (this.executorService != null) {
                return this.executorService;
            }
            this.executorService = new ThreadPoolExecutor(config.getCore(), config.getMax(), 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        }
        return this.executorService;
    }

    private IdempotentRequest buildRequest(Idempotent idempotent, MetaHolder metaHolder, Object[] arguments, IdempotentConfig config) {
        IdempotentRequest request = new IdempotentRequest();
        String lockName;
        String methodName = getMethodName(metaHolder.getBeanType(), metaHolder.getMethod());
        if (StringUtils.hasText(idempotent.value())) {
            lockName = getValueBySpel(idempotent.value(), metaHolder.getMethodParameters(), arguments);
        } else {
            lockName = methodName;
        }

        String key = null;
        if (StringUtils.hasText(idempotent.key())) {
            key = getValueBySpel(idempotent.key(), metaHolder.getMethodParameters(), arguments);
        } else if (arguments.length > 0) {
            StringBuilder bd = new StringBuilder(methodName).append(":");
            for (Object arg : arguments) {
                bd.append(String.valueOf(arg));
            }
            key = DigestUtils.md5DigestAsHex(bd.toString().getBytes(Charset.defaultCharset()));
        }
        Assert.notNull(key, "idempotent key is required.");

        request.setLockName(config.getLockPre() + lockName);
        request.setKey(key);
        request.setExpireTime(idempotent.lockExpireTime());
        request.setTryTimeout(idempotent.tryTimeout());
        request.setExpireTimeUnit(TimeUnit.MILLISECONDS);
        request.setUnAcceptErrors(idempotent.noStorageFor());
        if (idempotent.storageParams().length > 0) {
            request.setStoreConfigList(buildStorageConfig(idempotent.storageParams()));
        } else {
            request.setStoreConfigList(config.getStorage());
        }
        return request;
    }

    private String getMethodName(Class<?> beanType, Method method) {
        StringBuilder bd = new StringBuilder();
        return bd.append(beanType.getName()).append(".").append(method.getName()).toString();
    }

    private List<StorageConfig> buildStorageConfig(StorageParam[] storageParams) {
        int len = storageParams.length;
        List<StorageConfig> list = new ArrayList<>(len);
        StorageParam storageParam;
        for (int i = 0; i < len; i++) {
            storageParam = storageParams[i];
            StorageConfig storageConfig = new StorageConfig();
            storageConfig.setLevel(i + 1);
            storageConfig.setStorageModule(storageParam.storage());
            storageConfig.setExpireTime(storageParam.expireTime());
            storageConfig.setTimeUnit(storageParam.timeUnit());
            list.add(storageConfig);
        }
        return list;
    }

    private String getValueBySpel(String spel, MethodParameter[] methodParameters, Object[] arguments) {
        Object result;
        if (spel.contains("#")) {
            Expression expression = parser.parseExpression(spel);
            EvaluationContext context = new StandardEvaluationContext();
            for (MethodParameter parameter : methodParameters) {
                String parameterName = parameter.getParameterName();
                Assert.notNull(parameterName, "can not resolve parameter name of " + parameter);
                context.setVariable(parameterName, arguments[parameter.getParameterIndex()]);
            }
            result = expression.getValue(context);
        } else {
            result = spel;
        }
        if (result == null) {
            return null;
        }
        return result.toString();
    }

    static class InnerCommandAction implements CommandAction {

        private MethodInvocation invocation;

        public InnerCommandAction(MethodInvocation invocation) {
            this.invocation = invocation;
        }

        @Override
        public Object execute() throws MethodExecuteException {
            try {
                return invocation.proceed();
            } catch (Throwable throwable) {
                throw new MethodExecuteException(throwable);
            }
        }

        @Override
        public Object executeWithArgs(Object[] args) throws MethodExecuteException {
            return null;
        }
    }

}
