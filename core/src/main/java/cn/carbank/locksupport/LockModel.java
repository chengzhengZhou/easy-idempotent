package cn.carbank.locksupport;

/**
 * 锁的模式
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public enum LockModel {
    REENTRANT, FAIR, MULTIPLE, RED_LOCK, READ, WRITE;
}
