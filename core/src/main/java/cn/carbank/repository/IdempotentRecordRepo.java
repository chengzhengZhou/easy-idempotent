package cn.carbank.repository;

import cn.carbank.IdempotentRecord;

import java.util.concurrent.TimeUnit;

/**
 * 幂等记录
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public interface IdempotentRecordRepo {

    /**
     * 添加记录
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     * @return
     */
    boolean add(String key, String value, int expireTime, TimeUnit timeUnit);

    /**
     * key对应的记录是否存在
     * @param key
     * @return
     */
    boolean exist(String key);

    /**
     * 根据key获取记录
     * @param key
     * @return
     */
    IdempotentRecord get(String key);

    /**
     * 删除指定key的记录
     *
     * @param key
     * @return
     */
    boolean delete(String key);

}
