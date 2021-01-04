package cn.carbank.idempotent;

import cn.carbank.idempotent.constant.StorageType;
import cn.carbank.idempotent.repository.IdempotentRecordRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 从spring容器中获取存储类
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月19日
 */
public class SpringRecordRepositoryFactory extends RecordRepositoryFactory {

    private final Logger logger = LoggerFactory.getLogger(SpringRecordRepositoryFactory.class);

    private ApplicationContext applicationContext;

    @Override
    public Map<StorageType, IdempotentRecordRepo> getRecordRepository() {
        if (cache != null) {
            return cache;
        }
        String[] names = applicationContext.getBeanNamesForType(IdempotentRecordRepo.class);
        List<IdempotentRecordRepo> list = new ArrayList<>(names.length);
        for (String name : names) {
            list.add(applicationContext.getBean(name, IdempotentRecordRepo.class));
        }
        setMap(list);
        return super.getRecordRepository();
    }

    public void setMap(List<IdempotentRecordRepo> list) {
        if (list == null || list.size() <= 0) {
            logger.warn("can not find record repository from spring.");
            return;
        }
        Map<StorageType, List<IdempotentRecordRepo>> candidates = new HashMap<>();
        for (IdempotentRecordRepo item : list) {
            StorageType storageType = StorageType.ofName(item.getClass().getSimpleName());
            if (candidates.get(storageType) == null) {
                candidates.put(storageType, new ArrayList<IdempotentRecordRepo>());
            }
            candidates.get(storageType).add(item);
        }

        Map<StorageType, IdempotentRecordRepo> map = new HashMap<>();
        Iterator<StorageType> iterator = candidates.keySet().iterator();
        while(iterator.hasNext()) {
            StorageType next = iterator.next();
            map.put(next, getMainRepo(candidates.get(next)));
        }

        cache = map;
    }

    private IdempotentRecordRepo getMainRepo(List<IdempotentRecordRepo> idempotentRecordRepos) {
        if (idempotentRecordRepos.size() == 1) {
            return idempotentRecordRepos.get(0);
        }
        IdempotentRecordRepo primary = null;
        for (IdempotentRecordRepo repo : idempotentRecordRepos) {
            if (repo.getClass().isAnnotationPresent(Primary.class)) {
                if (primary == null) {
                    primary = repo;
                    continue;
                }
                throw new UnsupportedOperationException("more than one IdempotentRecordRepos annotated with @Primary. repos: " + idempotentRecordRepos);
            }
        }
        if (primary == null) {
            throw new UnsupportedOperationException("more than one IdempotentRecordRepos. you can add @Primary annotation on the one of them. repos: " + idempotentRecordRepos);
        }
        return primary;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
