package cn.carbank.idempotent;

import cn.carbank.idempotent.exception.MethodExecuteException;

/**
 * 方法包装，为了方便调用
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月18日
 */
public interface CommandAction {
    /**
     * 执行方法
     *
     * @return
     * @throws MethodExecuteException
     */
    Object execute() throws MethodExecuteException;

    /**
     * 传参执行
     *
     * @param args  方法参数
     * @return
     * @throws MethodExecuteException
     */
    Object executeWithArgs(Object[] args) throws MethodExecuteException;
}
