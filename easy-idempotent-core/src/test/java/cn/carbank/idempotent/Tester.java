package cn.carbank.idempotent;

import cn.carbank.idempotent.constant.StorageType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 请填写类注释
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月18日
 */
public class Tester {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                MyCommand command = new MyCommand("NO1001", "NO1001");
                try {
                    latch.await();
                    Thread.sleep((int) (Math.random() * 500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String execute = command.execute();
                System.out.println(execute);
            });
        }
        latch.countDown();
        LockSupport.park();
    }

    static class MyCommand extends IdempotentCommand<String> {

        MyCommand(String lock, String key) {
            super(Setter.withLock(lock).expireTime(1000)
                .tryTimeout(2000)
                .expireTimeUnit(TimeUnit.MILLISECONDS)
                .key(key)
                .noStorageException(new Class[]{IllegalArgumentException.class})
                .storageConfig(1, StorageType.MEMORY, 100, TimeUnit.MILLISECONDS));
        }

        @Override
        protected String run() throws Exception {
            Thread.sleep((int) (Math.random() * 200));
            return "success";
        }

        @Override
        protected String getIdempotentResult() {

            return "idempotent re";
        }
    }

}
