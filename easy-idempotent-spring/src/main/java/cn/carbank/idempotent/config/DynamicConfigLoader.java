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
package cn.carbank.idempotent.config;

import cn.carbank.idempotent.StorageConfig;
import cn.carbank.idempotent.constant.StorageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 配置文件加载
 *
 * @author chengzhengZhou
 * @since 2020年12月21日
 */
public abstract class DynamicConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(DynamicConfigLoader.class);
    private static final String DEFAULT_PROPERTY_FILE = "idempotent.properties";

    public static IdempotentConfig load() {
        IdempotentConfig config = new IdempotentConfig();
        Properties props = new Properties();
        try (InputStream in = DynamicConfigLoader.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTY_FILE)) {
            if (in != null) {
                props.load(in);
            }
        } catch (FileNotFoundException e) {
            logger.error("{} not found.", DEFAULT_PROPERTY_FILE);
        } catch (IOException e) {
            logger.error("read file {} error", DEFAULT_PROPERTY_FILE);
        }
        fillConfig(props, config);
        logger.info("load idempotent config success {}", config);
        return config;
    }

    private static void fillConfig(Properties props, IdempotentConfig config) {
        String lockPre = props.getProperty("idempotent.lock.pre", "lock:");
        String groupName = props.getProperty("idempotent.lock.group_name");
        String namespace = props.getProperty("idempotent.lock.namespace", "1");
        String core = props.getProperty("idempotent.core", "1");
        String max = props.getProperty("idempotent.max", "10");
        String storage = props.getProperty("idempotent.storage");

        List<StorageConfig> storageConfigList = new ArrayList<>();
        StorageConfig storageConfig;
        if (StringUtils.hasText(storage)) {
            String[] split = storage.split(",");
            for (int i = 0, j = 1; i < split.length; i++) {
                String item = split[i];
                if (StringUtils.hasText(item.trim())) {
                    storageConfig = new StorageConfig();
                    String pre = String.format("idempotent.%s.", item);
                    String expireTime = props.getProperty(pre + "expire_time");
                    String timeUnit = props.getProperty(pre + "time_unit");
                    Assert.notNull(expireTime, pre + "expire_time is required.");
                    Assert.notNull(timeUnit, pre + "time_unit is required.");
                    storageConfig.setStorageModule(StorageType.valueOf(item.toUpperCase()));
                    storageConfig.setLevel(j);
                    storageConfig.setExpireTime(Integer.valueOf(expireTime));
                    storageConfig.setTimeUnit(TimeUnit.valueOf(timeUnit.toUpperCase()));
                    storageConfigList.add(storageConfig);
                    j++;
                }
            }
        }
        if (storageConfigList.size() <= 0) {
            storageConfig = new StorageConfig();
            storageConfig.setLevel(1);
            storageConfig.setExpireTime(1);
            storageConfig.setTimeUnit(TimeUnit.MINUTES);
            storageConfig.setStorageModule(StorageType.REDIS);
            storageConfigList.add(storageConfig);
        }

        config.setLockPre(lockPre);
        config.setGroupName(groupName);
        config.setNamespace(Integer.valueOf(namespace));
        Integer coreSize = Integer.valueOf(core);
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        config.setCore(Math.min(coreSize, availableProcessors));
        config.setMax(Integer.valueOf(max));
        config.setStorage(storageConfigList);
    }
}
