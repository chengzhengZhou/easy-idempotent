package cn.carbank.locksupport;

/**
 * 锁客户端
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public interface LockClient {

    Lock getLock(String lockName, LockModel lockModel);

}
