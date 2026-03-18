package cn.carbank.idempotent.exception;

/**
 * 执行异常
 *
 * @author chengzhengZhou
 * @since 2020年12月17日
 */
public class IdempotentRuntimeException extends RuntimeException {

    private Throwable error;

    public IdempotentRuntimeException(String message) {
        super(message);
    }

    public IdempotentRuntimeException(String message, Throwable error) {
        super(message);
        this.error = error;
    }

    public IdempotentRuntimeException(Throwable cause) {
        super(cause);
    }

    public Throwable getError() {
        return error;
    }
}
