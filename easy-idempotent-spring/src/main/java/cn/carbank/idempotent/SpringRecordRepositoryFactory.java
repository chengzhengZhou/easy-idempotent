/*
 * Copyright 2020 chengzhengZhou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 从spring容器中获取存储类
 *
 * @author chengzhengZhou
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
        List<IdempotentRecordRepo> list = Stream.of(names).map(
            (beanName) -> applicationContext.getBean(beanName, IdempotentRecordRepo.class))
            .collect(Collectors.toList());
        setMap(list);
        return super.getRecordRepository();
    }

    public void setMap(List<IdempotentRecordRepo> list) {
        if (list == null || list.size() <= 0) {
            logger.warn("can not find record repository from spring.");
            return;
        }
        Map<StorageType, List<IdempotentRecordRepo>> candidates = new HashMap<>();
        list.forEach((item) -> {
            StorageType storageType = StorageType.ofName(item.getClass().getSimpleName());
            if (candidates.get(storageType) == null) {
                candidates.put(storageType, new ArrayList<>());
            }
            candidates.get(storageType).add(item);
        });

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
