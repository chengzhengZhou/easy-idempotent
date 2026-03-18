# easy-idempotent
简单幂等组件


## 项目蓝图
easy-idempotent 是一个轻量级幂等组件，面向 Spring 开发者：你只需要在业务方法上增加 `@Idempotent`，框架会自动处理并发下的锁控制与幂等 key 判断；当重复请求发生时，会调用你指定的 `idempotentMethod` 作为“重复请求处理逻辑”。

### 模块结构
- `easy-idempotent-core`：幂等核心编排（锁/存储抽象、幂等请求构建、执行链路）
- `easy-idempotent-spring`：Spring AOP 实现（`IdempotentInterceptor`、`@Idempotent` 元数据解析）
- `easy-idempotent-starter`：Spring Boot 自动配置与默认 Redis Bean

### 运行链路
1. 拦截被 `@Idempotent` 标注的方法（`IdempotentInterceptor`）
2. 生成 `lockName` 与 `key`
   - `lockName`：`@Idempotent.value` 为空时使用“方法全限定名”（支持 SpEL）
   - `key`：`@Idempotent.key` 为空时基于方法入参生成 MD5（支持 SpEL）
3. 重复请求时调用同一 bean 内 `idempotentMethod` 指定的方法

### 快速开始（Spring Boot）
引入 starter：
```xml
<dependency>
  <groupId>cn.carbank</groupId>
  <artifactId>easy-idempotent-starter</artifactId>
  <version>2.1.4.RELEASE</version>
</dependency>
```

使用注解：
```java
@Service
public class OrderService {

  @Idempotent(
      idempotentMethod = "payDuplicate",
      key = "#orderId",
      lockExpireTime = 10000
  )
  public String pay(String orderId) {
    return "OK";
  }

  public String payDuplicate(String orderId) {
    return "DUPLICATE";
  }
}
```

### 扩展点（按需替换实现）
- 自定义锁：实现 `cn.carbank.idempotent.locksupport.LockClient` 并在 Spring 容器中提供 Bean（由 `SpringLockClientFactory` 获取）
- 自定义存储：实现 `cn.carbank.idempotent.repository.IdempotentRecordRepo` 并在 Spring 容器中提供 Bean（由 `SpringRecordRepositoryFactory` 收集映射到 `StorageType`）

### License

This project is licensed under the Apache License, Version 2.0. See `LICENSE`.

