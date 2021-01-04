package cn.carbank.idempotent;

/**
 * 拦截通知
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月15日
 */
public interface MethodInterceptor {

    boolean preProcess(IdempotentRequest methodInfo);

    void postProcess(Object re, IdempotentRequest methodInfo);

    void errorProcess(Exception exception, IdempotentRequest methodInfo);
}
