package cn.carbank;

import cn.carbank.annotation.Idempotent;
import cn.carbank.config.DynamicConfigLoader;
import cn.carbank.config.IdempotentConfig;
import cn.carbank.exception.IdempotentRuntimeException;
import cn.carbank.exception.MethodExecuteException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * 拦截器
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月16日
 */
public class IdempotentInterceptor implements MethodInterceptor, ApplicationContextAware, InitializingBean {

    private static final MetaHolderFactory metaHolderFactory;
    private IdempotentConfig config;
    private ApplicationContext applicationContext;
    static {
        metaHolderFactory = new MetaHolderFactory();
    }

    public IdempotentInterceptor() {
    }

    public IdempotentInterceptor(IdempotentConfig config) {
        this.config = config;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (!method.isAnnotationPresent(Idempotent.class)) {
            throw new IllegalStateException("method should be annotated with Idempotent.");
        }
        MetaHolder metaHolder = metaHolderFactory.create(invocation);
        IdempotentInvokable idempotentInvokable = IdempotentInvokableFactory.getInstance().create(metaHolder, invocation, config, applicationContext);
        Object result;
        try {
            result = idempotentInvokable.execute();
        } catch (MethodExecuteException e) {
            throw e.getCause();
        } catch (IdempotentRuntimeException e) {
            if (e.getError() != null) {
                throw e.getError();
            }
            throw e;
        }
        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.config == null) {
            this.config = DynamicConfigLoader.load();
        }
    }

    private static class MetaHolderFactory {

        public MetaHolder create(MethodInvocation invocation) {
            Object proxy = invocation.getThis();
            Method method = invocation.getMethod();
            return create(proxy, method);
        }

        private MetaHolder create(Object proxy, Method method) {
            Idempotent idempotent = AnnotationUtils.findAnnotation(method, Idempotent.class);
            MetaHolder metaHolder = new MetaHolder(proxy, method);
            metaHolder.setIdempotent(idempotent);

            Class<?>[] parameterTypes = method.getParameterTypes();
            Method idempotentMethod = ClassUtils.getMethod(metaHolder.getBeanType(), idempotent.idempotentMethod(), parameterTypes);
            Assert.notNull(idempotentMethod, "Idempotent method is required.");
            metaHolder.setIdempotentMethod(idempotentMethod);
            return metaHolder;
        }
    }
}
