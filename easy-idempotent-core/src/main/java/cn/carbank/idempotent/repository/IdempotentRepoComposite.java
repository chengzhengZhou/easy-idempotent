package cn.carbank.idempotent.repository;

import cn.carbank.idempotent.StorageConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

/**
 * 存储聚合操作
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public class IdempotentRepoComposite {

    private static final Logger logger = LoggerFactory.getLogger(IdempotentRepoComposite.class);
    private final Map<String, IdempotentRecordRepo> map = new HashMap<>();

    private final ExecutorService executor;

    public IdempotentRepoComposite(ExecutorService executor) {
        if (executor == null) {
            this.executor = ForkJoinPool.commonPool();
        } else {
            this.executor = executor;
        }
    }

    public void addRepo(String storeModuleName, IdempotentRecordRepo idempotentRecordRepo) {
        map.put(storeModuleName, idempotentRecordRepo);
    }

    public boolean add(String key, String value, List<StorageConfig> configList) {
        if (configList.size() == 1) {
            StorageConfig config = configList.get(0);
            return map.get(config.getStorageModule().name()).add(key, value, config.getExpireTime(), config.getTimeUnit());
        }
        CompletableFuture<Boolean>[] futures = new CompletableFuture[configList.size()];
        for (int i = 0; i < configList.size(); i++) {
            StorageConfig config = configList.get(i);
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
                () -> map.get(config.getStorageModule().name()).add(key, value, config.getExpireTime(), config.getTimeUnit()),
                executor);
            futures[i] = future;
        }
        return merge(futures);
    }

    public boolean exist(String key, List<StorageConfig> configList) {
        if (configList.size() == 1) {
            StorageConfig config = configList.get(0);
            return map.get(config.getStorageModule().name()).exist(key);
        }
        CompletableFuture<Boolean>[] futures = new CompletableFuture[configList.size()];
        for (int i = 0; i < configList.size(); i++) {
            StorageConfig config = configList.get(i);
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
                () -> map.get(config.getStorageModule().name()).exist(key),
                executor);
            futures[i] = future;
        }
        return merge(futures);
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    private boolean merge(CompletableFuture<Boolean>[] futures) {
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futures);
        try {
            combinedFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("merge future fail 【{}】", combinedFuture, e);
        }

        return Stream.of(futures).map(CompletableFuture::join).filter((val) -> val == true).count() > 0;
    }
}
