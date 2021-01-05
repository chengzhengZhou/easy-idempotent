package cn.carbank.idempotent;

import cn.carbank.idempotent.annotation.Idempotent;
import cn.carbank.idempotent.config.DynamicConfigLoader;
import cn.carbank.idempotent.config.IdempotentConfig;
import cn.carbank.idempotent.exception.IdempotentRuntimeException;
import cn.carbank.idempotent.exception.MethodExecuteException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
            valid();
        }
    }

    private void valid() {
        Assert.notNull(this.config.getGroupName(), "idempotent.lock.group_name is required.");
    }

    private static class MetaHolderFactory {
        private final Class<Idempotent> annotationClass = Idempotent.class;
        private final Map<Method, MetaHolder> registry = new ConcurrentHashMap<>();

        public MetaHolder create(MethodInvocation invocation) {
            Object proxy = invocation.getThis();
            Method method = invocation.getMethod();
            MetaHolder metaHolder = registry.get(method);
            if (metaHolder == null) {
                metaHolder = create(proxy, method);
                registry.put(method, metaHolder);
            }
            return metaHolder;
        }

        private MetaHolder create(Object proxy, Method method) {
            Method targetMethod = getTargetMethod(method, AopUtils.getTargetClass(proxy));
            Idempotent idempotent = null;
            if ((idempotent = AnnotationUtils.findAnnotation(targetMethod, annotationClass)) == null) {
                throw new IllegalStateException("method should be annotated with Idempotent.");
            }

            MetaHolder metaHolder = new MetaHolder(proxy, targetMethod);
            metaHolder.setIdempotent(idempotent);

            Class<?>[] parameterTypes = method.getParameterTypes();
            Method idempotentMethod = getMethod(metaHolder.getBeanType(), idempotent.idempotentMethod(), parameterTypes);
            Assert.notNull(idempotentMethod, "Idempotent method is required.");
            metaHolder.setIdempotentMethod(BridgeMethodResolver.findBridgedMethod(idempotentMethod));
            return metaHolder;
        }

        private Method getTargetMethod(Method method, Class targetClass) {
            if (method.isAnnotationPresent(annotationClass)) {
                return method;
            } else {
                Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
                if (specificMethod != method && specificMethod.isAnnotationPresent(annotationClass)) {
                    return specificMethod;
                }
            }
            return null;
        }

        private Method getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
            Method[] methods = type.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameterTypes)) {
                    return method;
                }
            }
            Class<?> superClass = type.getSuperclass();
            if (superClass != null && !superClass.equals(Object.class)) {
                return getMethod(superClass, name, parameterTypes);
            } else {
                return null;
            }
        }
    }
}
