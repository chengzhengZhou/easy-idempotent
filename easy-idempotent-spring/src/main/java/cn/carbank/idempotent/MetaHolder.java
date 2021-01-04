package cn.carbank.idempotent;

import cn.carbank.idempotent.annotation.Idempotent;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 元数据信息
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月18日
 */
public class MetaHolder {

    private Idempotent idempotent;
    private Method idempotentMethod;
    private final Object bean;
    private final Class<?> beanType;
    private final Method method;
    private final Method bridgedMethod;
    private final MethodParameter[] parameters;
    private final ParameterNameDiscoverer parameterNameDiscoverer;

    public MetaHolder(Object bean, Method method) {
        Assert.notNull(bean, "Bean is required");
        Assert.notNull(method, "Method is required");
        this.bean = bean;
        this.method = method;
        this.parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        this.beanType = ClassUtils.getUserClass(bean);
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = this.initMethodParameters();
    }

    public Idempotent getIdempotent() {
        return idempotent;
    }

    public void setIdempotent(Idempotent idempotent) {
        this.idempotent = idempotent;
    }

    public Method getIdempotentMethod() {
        return idempotentMethod;
    }

    public void setIdempotentMethod(Method idempotentMethod) {
        this.idempotentMethod = idempotentMethod;
    }

    public Object getBean() {
        return this.bean;
    }

    /**
     * 执行用户业务逻辑的方法
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * If the bean method is a bridge method, this method returns the bridged
     * (user-defined) method. Otherwise it returns the same method as {@link #getMethod()}.
     */
    protected Method getBridgedMethod() {
        return this.bridgedMethod;
    }

    /**
     * 方法参数信息
     *
     * @return
     */
    public MethodParameter[] getMethodParameters() {
        return this.parameters;
    }

    /**
     * 返回当前方法所在的类
     * <p>如果当前类是通过CGLIB生成的，则返回其代理的类
     */
    public Class<?> getBeanType() {
        return this.beanType;
    }

    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
        return AnnotationUtils.findAnnotation(this.method, annotationType);
    }

    private MethodParameter[] initMethodParameters() {
        int parameterCount = this.bridgedMethod.getParameterTypes().length;
        MethodParameter[] result = new MethodParameter[parameterCount];

        for(int i = 0; i < parameterCount; ++i) {
            HandlerMethodParameter parameter = new MetaHolder.HandlerMethodParameter(i);
            //GenericTypeResolver.resolveParameterType(parameter, this.beanType);
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            result[i] = parameter;
        }

        return result;
    }

    private class HandlerMethodParameter extends MethodParameter {
        protected HandlerMethodParameter(int index) {
            super(MetaHolder.this.bridgedMethod, index);
        }

        public Class<?> getDeclaringClass() {
            return MetaHolder.this.getBeanType();
        }

        public <T extends Annotation> T getMethodAnnotation(Class<T> annotationType) {
            return MetaHolder.this.getMethodAnnotation(annotationType);
        }
    }
}
