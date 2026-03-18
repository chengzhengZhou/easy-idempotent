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

import cn.carbank.idempotent.locksupport.LockClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * 锁客户端获取工厂
 * 从spring容器中获取
 *
 * @author chengzhengZhou
 * @since 2020年12月19日
 */
public class SpringLockClientFactory extends LockClientFactory {

    private final Logger logger = LoggerFactory.getLogger(SpringLockClientFactory.class);

    private ApplicationContext applicationContext;

    @Override
    public LockClient getClient() {
        if (lockClient != null) {
            return lockClient;
        }
        try {
            lockClient = applicationContext.getBean(LockClient.class);
        } catch (Exception e) {
            logger.warn("can not find lock client from spring.");
        }
        return super.getClient();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
