package cn.carbank.idempotent.exception;

/**
 * 参数异常
 *
 * @author chengzhengZhou
 * @since 2020年12月17日
 */
public class IdempotentBadRequestException extends RuntimeException {

    public IdempotentBadRequestException(String message) {
        super(message);
    }
}
