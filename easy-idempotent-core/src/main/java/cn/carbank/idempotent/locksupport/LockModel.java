package cn.carbank.idempotent.locksupport;

/**
 * 锁的模式
 *
 * @author chengzhengZhou
 * @since 2020年12月14日
 */
public enum LockModel {
    REENTRANT, FAIR, MULTIPLE, RED_LOCK, READ, WRITE;
}
