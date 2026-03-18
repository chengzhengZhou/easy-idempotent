package cn.carbank.idempotent.locksupport;

/**
 * 锁客户端
 *
 * @author chengzhengZhou
 * @since 2020年12月14日
 */
public interface LockClient {

    Lock getLock(String lockName, LockModel lockModel);

}
