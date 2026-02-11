# easy-query 插入数据无效问题排查

## 问题描述

使用 easy-query 框架插入数据后，在数据库中找不到插入的数据，或者查询不到插入的数据，但 easy-query 的其他查询操作是正常的。

## 排查步骤

### 步骤 1：检查配置

确保 easy-query 的 `print-sql` 配置为 `true`，这样可以在日志中查看执行的 SQL 语句：

```yaml
easy-query:
  print-sql: true
```

### 步骤 2：检查插入代码

如果日志中没有打印插入的 SQL 语句，检查 easy-query 的插入代码是否正确。

**错误示例**：
```java
easEntityQuery.insertable(tenant); // 错误：只是创建了插入语句对象，没有执行插入操作
```

**正确示例**：
```java
// 执行插入并返回影响的行数
long rows = easyEntityQuery.insertable(tenant).executeRows();

// 其他方法请查看easy-query官方文档
```

### 步骤 3：检查 SQL 执行日志

如果日志中打印了插入的 SQL 语句和参数，但没有打印插入的行数，说明插入操作失败了。

**日志示例**：
```
c.e.q.core.util.EasyJdbcExecutorUtil     : ==> Preparing: INSERT INTO "lb_tenant" ("id","is_deleted","created_at","created_by","updated_at","version_id","name","is_default","is_active","is_system") VALUES (?,?,?,?,?,?,?,?,?,?)
c.e.q.core.util.EasyJdbcExecutorUtil     : ==> Parameters: 264eeab7-63a4-4141-9c2e-82ab0416827b(UUID),false(Boolean),2026-02-11T09:45:56.207995900(LocalDateTime),880789e9-68cc-443c-a252-8aa00fb8127b(UUID),2026-02-11T09:45:56.207995900(LocalDateTime),0(Integer),spring+easy-query demo(String),false(Boolean),false(Boolean),false(Boolean)
```

**正常情况下应该有**：
```
c.e.q.core.util.EasyJdbcExecutorUtil     : <== Total: 1
```

### 步骤 4：检查异常处理

如果插入操作失败但没有看到错误信息，可能是因为异常没有被正确捕获和处理。请检查项目的异常处理系统是否完善。

**注意**：如果是正式项目，不建议在插入操作周围添加 try-catch 块去捕获异常，应该使用全局异常统一处理机制；如果是非正式项目或调试过程，可以参考以下代码：

```java
try {
    long rows = easyEntityQuery.insertable(tenant).executeRows();
    System.out.println("插入成功，影响行数：" + rows);
} catch (Exception e) {
    System.err.println("插入失败：" + e.getMessage());
    e.printStackTrace();
}
```

## 常见原因及解决方案

### 原因 1：没有执行插入操作

**症状**：日志中没有打印插入的 SQL 语句。
**解决方案**：使用 `executeRows()` 或其他执行方法执行插入操作。

### 原因 2：数据库约束违反

**症状**：插入操作失败，可能会有约束违反的异常。
**解决方案**：检查数据库表的约束，确保插入的数据符合约束要求。

### 原因 3：事务回滚

**症状**：插入操作执行成功，但数据在数据库中不可见。
**解决方案**：检查是否有事务回滚的情况，确保事务正确提交。

### 原因 4：实体映射问题

**症状**：插入操作执行成功，但某些字段没有正确插入。
**解决方案**：检查实体类的注解和映射配置，确保所有需要插入的字段都正确映射。

## 最佳实践

1. **始终执行插入操作**：使用 `executeRows()` 或其他执行方法执行插入操作，不要只创建插入语句对象。

2. **启用 SQL 日志**：在开发和测试环境中启用 `print-sql` 配置，便于排查问题。

3. **添加异常处理**：添加适当的全局异常处理，确保能够捕获和处理异常。

4. **验证插入结果**：检查插入操作的返回值（如影响的行数），验证插入是否成功。

5. **参考官方文档**：遇到问题时，参考 easy-query 的官方文档和示例代码。

## 相关文档

- [easy-query 官方文档](https://www.easy-query.com/easy-query-doc/)
- [全局异常处理](../04-Spring框架/02-异常处理/01-全局异常处理.md)
