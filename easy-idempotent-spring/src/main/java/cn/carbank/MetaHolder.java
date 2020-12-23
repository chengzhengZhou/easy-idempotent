package cn.carbank;

import cn.carbank.annotation.Idempotent;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    @Nullable
    private volatile List<Annotation[][]> interfaceParameterAnnotations;
    private final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer;

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

    /**
     * Return a single annotation on the underlying method traversing its super methods
     * if no annotation can be found on the given method itself.
     * <p>Also supports <em>merged</em> composed annotations with attribute
     * overrides as of Spring Framework 4.2.2.
     * @param annotationType the type of annotation to introspect the method for
     * @return the annotation, or {@code null} if none found
     * @see AnnotatedElementUtils#findMergedAnnotation
     */
    @Nullable
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.findMergedAnnotation(this.method, annotationType);
    }

    /**
     * Return whether the parameter is declared with the given annotation type.
     * @param annotationType the annotation type to look for
     * @since 4.3
     * @see AnnotatedElementUtils#hasAnnotation
     */
    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.hasAnnotation(this.method, annotationType);
    }

    private MethodParameter[] initMethodParameters() {
        int count = this.bridgedMethod.getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            HandlerMethodParameter parameter = new HandlerMethodParameter(i);
            GenericTypeResolver.resolveParameterType(parameter, this.beanType);
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            result[i] = parameter;
        }
        return result;
    }

    private List<Annotation[][]> getInterfaceParameterAnnotations() {
        List<Annotation[][]> parameterAnnotations = this.interfaceParameterAnnotations;
        if (parameterAnnotations == null) {
            parameterAnnotations = new ArrayList<>();
            for (Class<?> ifc : this.method.getDeclaringClass().getInterfaces()) {
                for (Method candidate : ifc.getMethods()) {
                    if (isOverrideFor(candidate)) {
                        parameterAnnotations.add(candidate.getParameterAnnotations());
                    }
                }
            }
            this.interfaceParameterAnnotations = parameterAnnotations;
        }
        return parameterAnnotations;
    }

    private boolean isOverrideFor(Method candidate) {
        if (!candidate.getName().equals(this.method.getName()) ||
        candidate.getParameterCount() != this.method.getParameterCount()) {
            return false;
        }
        Class<?>[] paramTypes = this.method.getParameterTypes();
        if (Arrays.equals(candidate.getParameterTypes(), paramTypes)) {
            return true;
        }
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i] !=
            ResolvableType.forMethodParameter(candidate, i, this.method.getDeclaringClass()).resolve()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 增加特性，获取参数接口的注解信息
     */
    protected class HandlerMethodParameter extends SynthesizingMethodParameter {

        @Nullable
        private volatile Annotation[] combinedAnnotations;

        public HandlerMethodParameter(int index) {
            super(MetaHolder.this.bridgedMethod, index);
        }

        protected HandlerMethodParameter(HandlerMethodParameter original) {
            super(original);
        }

        @Override
        public Class<?> getContainingClass() {
            return MetaHolder.this.getBeanType();
        }

        @Override
        public <T extends Annotation> T getMethodAnnotation(Class<T> annotationType) {
            return MetaHolder.this.getMethodAnnotation(annotationType);
        }

        @Override
        public <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationType) {
            return MetaHolder.this.hasMethodAnnotation(annotationType);
        }

        @Override
        public Annotation[] getParameterAnnotations() {
            Annotation[] anns = this.combinedAnnotations;
            if (anns == null) {
                anns = super.getParameterAnnotations();
                int index = getParameterIndex();
                if (index >= 0) {
                    for (Annotation[][] ifcAnns : getInterfaceParameterAnnotations()) {
                        if (index < ifcAnns.length) {
                            Annotation[] paramAnns = ifcAnns[index];
                            if (paramAnns.length > 0) {
                                List<Annotation> merged = new ArrayList<>(anns.length + paramAnns.length);
                                merged.addAll(Arrays.asList(anns));
                                for (Annotation paramAnn : paramAnns) {
                                    boolean existingType = false;
                                    for (Annotation ann : anns) {
                                        if (ann.annotationType() == paramAnn.annotationType()) {
                                            existingType = true;
                                            break;
                                        }
                                    }
                                    if (!existingType) {
                                        merged.add(adaptAnnotation(paramAnn));
                                    }
                                }
                                anns = merged.toArray(new Annotation[0]);
                            }
                        }
                    }
                }
                this.combinedAnnotations = anns;
            }
            return anns;
        }

        @Override
        public HandlerMethodParameter clone() {
            return new HandlerMethodParameter(this);
        }
    }
}
