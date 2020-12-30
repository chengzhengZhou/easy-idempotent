package cn.carbank.idempotent.utils;

/**
 * 异常处理
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月18日
 */
public class Exceptions {

    /**
     * 将异常装换为运行时异常，以便代码中不用抛出
     *
     * @param t
     * @return
     */
    public static RuntimeException sneakyThrow(Throwable t) {
        return Exceptions.<RuntimeException>doThrow(t);
    }

    private static <T extends Throwable> T doThrow(Throwable ex) throws T {
        throw (T) ex;
    }
}
