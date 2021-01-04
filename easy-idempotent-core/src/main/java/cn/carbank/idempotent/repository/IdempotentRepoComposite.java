package cn.carbank.idempotent.repository;

import cn.carbank.idempotent.StorageConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 存储聚合操作
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public class IdempotentRepoComposite {

    private final Logger logger = LoggerFactory.getLogger(IdempotentRepoComposite.class);
    private final Map<String, IdempotentRecordRepo> map = new HashMap<>();

    private final ExecutorService executor;

    public IdempotentRepoComposite(ExecutorService executor) {
        if (executor == null) {
            this.executor = Executors.newFixedThreadPool(Math.min(10, Runtime.getRuntime().availableProcessors()));
        } else {
            this.executor = executor;
        }
    }

    public void addRepo(String storeModuleName, IdempotentRecordRepo idempotentRecordRepo) {
        map.put(storeModuleName, idempotentRecordRepo);
    }

    public boolean add(final String key, final String value, List<StorageConfig> configList) {
        if (configList.size() == 1) {
            StorageConfig config = configList.get(0);
            return map.get(config.getStorageModule().name()).add(key, value, config.getExpireTime(), config.getTimeUnit());
        }
        Future<Boolean>[] futures = new Future[configList.size()];
        for (int i = 0; i < configList.size(); i++) {
            final StorageConfig config = configList.get(i);
            futures[i] = executor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return map.get(config.getStorageModule().name()).add(key, value, config.getExpireTime(), config.getTimeUnit());
                }
            });
        }
        return merge(futures);
    }

    public boolean exist(final String key, List<StorageConfig> configList) {
        if (configList.size() == 1) {
            StorageConfig config = configList.get(0);
            return map.get(config.getStorageModule().name()).exist(key);
        }
        Future<Boolean>[] futures = new Future[configList.size()];
        for (int i = 0; i < configList.size(); i++) {
            final StorageConfig config = configList.get(i);
            futures[i] = executor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return map.get(config.getStorageModule().name()).exist(key);
                }
            });
        }
        return merge(futures);
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    private boolean merge(Future<Boolean>[] futures) {
        boolean isDone = false;
        Future<Boolean>[] newFutures = futures;
        int count = 10;
        while(!isDone) {
            if (newFutures.length <= 0) {
                break;
            }
            for (int i = 0 ; i < newFutures.length; i++) {
                Future<Boolean> f = newFutures[i];
                if (f.isDone() || count < 0) {
                    try {
                        if (f.get()) {
                            isDone = true;
                        } else {
                            // 移除
                            f = null;
                            newFutures = removeIdx(newFutures, i);
                        }
                        break;
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("merge future fail 【{}】", f, e);
                    }
                }
            }
            count--;
        }
        return isDone;
    }

    private Future<Boolean>[] removeIdx(Future<Boolean>[] fs, int idx) {
        Future<Boolean>[] newFs = new Future[fs.length - 1];
        if (newFs.length > 0) {
            for (int i = 0, j = 0; i < fs.length; i++) {
                if (i != idx) {
                    newFs[j] = fs[i];
                    j++;
                }
            }
        }
        return newFs;
    }
}
