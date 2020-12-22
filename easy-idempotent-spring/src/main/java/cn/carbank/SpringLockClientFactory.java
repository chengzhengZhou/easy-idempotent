package cn.carbank;

import cn.carbank.locksupport.LockClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * 锁客户端获取工厂
 * 从spring容器中获取
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
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
