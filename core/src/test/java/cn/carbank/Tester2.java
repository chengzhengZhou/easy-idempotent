package cn.carbank;

import cn.carbank.constant.StorageType;

import java.util.concurrent.TimeUnit;

/**
 * 请填写类注释
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月18日
 */
public class Tester2 {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            MyCommand command = new MyCommand("NO1001", "NO1001");
            String execute = command.execute();
            System.out.println(execute);
        }
    }

    static class MyCommand extends IdempotentCommand<String> {

        MyCommand(String lock, String key) {
            super(Setter.withLock(lock).expireTime(10000)
                .tryTimeout(8000)
                .expireTimeUnit(TimeUnit.MILLISECONDS)
                .key(key)
                .noStorageException(new Class[]{IllegalArgumentException.class})
                .storageConfig(1, StorageType.MEMORY, 0, TimeUnit.DAYS));
        }

        @Override
        protected String run() throws Exception {

            return "success";
        }

        @Override
        protected String getIdempotentResult() {

            return "idempotent re";
        }
    }

}
