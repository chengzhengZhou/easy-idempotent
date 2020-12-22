package cn.carbank;

import cn.carbank.constant.StorageType;
import cn.carbank.repository.IdempotentRecordRepo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 数据存储类获取
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月18日
 */
public class RecordRepositoryFactory {

    private static final RecordRepositoryFactory INSTANCE = new RecordRepositoryFactory();

    public RecordRepositoryFactory() {
    }

    public static RecordRepositoryFactory getInstance() {
        return INSTANCE;
    }

    protected volatile Map<StorageType, IdempotentRecordRepo> cache = null;

    public Map<StorageType, IdempotentRecordRepo> getRecordRepository() {
        if (cache != null) {
            return cache;
        }
        synchronized(RecordRepositoryFactory.class) {
            if (cache != null) {
                return cache;
            }
            ServiceLoader<IdempotentRecordRepo> repos = ServiceLoader.load(IdempotentRecordRepo.class);
            Iterator<IdempotentRecordRepo> iterator = repos.iterator();
            Map<StorageType, IdempotentRecordRepo> map = new HashMap<>();
            while(iterator.hasNext()) {
                IdempotentRecordRepo next = iterator.next();
                map.put(StorageType.ofName(next.getClass().getSimpleName()), next);
            }
            cache = map;
        }
        return cache;
    }

}
