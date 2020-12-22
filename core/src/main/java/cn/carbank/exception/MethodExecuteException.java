package cn.carbank.exception;

/**
 * 方法执行异常
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月15日
 */
public class MethodExecuteException extends RuntimeException {
    public MethodExecuteException() {
    }

    public MethodExecuteException(String message) {
        super(message);
    }

    public MethodExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodExecuteException(Throwable cause) {
        super(cause);
    }
}
