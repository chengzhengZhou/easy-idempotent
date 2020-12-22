package cn.carbank;

import cn.carbank.constant.StorageType;
import cn.carbank.repository.IdempotentRecordRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<IdempotentRecordRepo> list = Stream.of(names).map((beanName) -> applicationContext.getBean(beanName, IdempotentRecordRepo.class)).collect(Collectors.toList());
        setMap(list);
        return super.getRecordRepository();
    }

    public void setMap(List<IdempotentRecordRepo> list) {
        if (list == null || list.size() <= 0) {
            logger.warn("can not find record repository from spring.");
            return;
        }
        Map<StorageType, IdempotentRecordRepo> map = new HashMap<>();
        list.forEach((item) -> {
            map.put(StorageType.ofName(item.getClass().getSimpleName()), item);
        });
        cache = map;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
