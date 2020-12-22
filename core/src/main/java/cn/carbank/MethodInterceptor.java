package cn.carbank;

/**
 * 拦截通知
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月15日
 */
public interface MethodInterceptor {

    default boolean preProcess(IdempotentRequest methodInfo) {
        return true;
    }

    default void postProcess(Object re, IdempotentRequest methodInfo) {

    }

    default void errorProcess(Exception exception, IdempotentRequest methodInfo) {

    }
}
