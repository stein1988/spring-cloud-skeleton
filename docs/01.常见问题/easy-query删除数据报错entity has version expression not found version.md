easy-query删除数据报错entity has version expression not found version排查

## 问题描述

使用 easy-query 框架删除数据时，报错提示 `entity:XXX has version expression not found version`。

## 排查步骤

### 步骤 1：检查entity类（包括基类）

确保 entity 类使用了版本字段注解（如 `@version`）
确保 entity 类使用了逻辑删除注解（如 `@LogicDelete`）

### 步骤 2：检查删除代码

确保删除代码中使用了表达式删除，例如：

```java
easyEntityQuery.deletable(Tenant.class).whereById(id).executeRows();
```

## 原因及解决方案

### 原因：删除时版本字段缺失

1、在使用逻辑删除时，删除操作实际上是更新操作，而不是物理删除。

2、在定义了版本字段的情况下，更新操作的条件中需要包含版本字段，以确保数据的一致性。

### 最佳实践

1. 优先使用实体对象删除，例如：

```java
Tenant tenant = easyEntityQuery.queryable(Tenant.class).whereById(id).firstOrNull();
if (tenant == null) {
    throw new EntityNotFoundException("Tenant not found");
}
easyQuery.deletable(tenant).executeRows();
```

在领域驱动设计中，建议先查出实体对象，再使用实体对象进行删除操作，而不是直接使用表达式删除。

2、表达式删除时包含版本字段，例如：

```java
easyQuery.deletable(Tenant.class)
         .withVersion(version)           // 当前版本为version
         .whereById(id).executeRows();
```

这样操作要求先查询出实体对象，再使用实体对象的版本字段进行删除操作，其实不如上面的实体对象删除操作方便。

3、谨慎使用ignoreVersion，忽略版本字段检查，例如：

```java
easyQuery.deletable(Tenant.class)
         .ignoreVersion()          // 忽略版本字段检查
         .whereById(id).executeRows();
```

**注意**：使用ignoreVersion时，删除操作将不检查版本字段，可能会导致数据不一致，千万要谨慎使用。

## 相关文档

- [easy-query 官方文档|高级功能|乐观锁版本号](https://www.easy-query.com/easy-query-doc/adv/version.html#%E9%80%BB%E8%BE%91%E5%88%A0%E9%99%A4%E5%8A%A0%E7%89%88%E6%9C%AC%E5%8F%B7)
