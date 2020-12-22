package cn.carbank.exception;

/**
 * 参数异常
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月17日
 */
public class IdempotentBadRequestException extends RuntimeException {

    public IdempotentBadRequestException(String message) {
        super(message);
    }
}
