# UUID字段规范

## 1. 概述

UUID（Universally Unique Identifier）是一种用于标识信息的128位数字，在分布式系统中广泛使用。本规范定义了项目中UUID字段的使用方式、版本选择以及与其他ID方案的对比。

### 1.1 项目背景

我们公司的项目架构设计需要同时支持以下场景：
- **云服务部署**：面向互联网用户的SaaS服务
- **局域网本地化部署**：在企业内部网络环境中独立运行，可能完全不联网
- **混合部署模式**：部分组件云化，部分组件本地化

在局域网环境中，服务器通常与内网NTP服务器同步时间，但由于网络不稳定、硬件时钟偏差等原因，经常会出现时间回退现象。这种情况下，依赖时间戳的ID生成方案（如雪花ID）会面临严重的ID冲突风险。

### 1.2 选择UUIDv7的必要性

基于项目的特殊需求，我们选择UUIDv7作为统一的ID生成方案，主要考虑以下因素：
- **解决时间回退问题**：UUIDv7虽然包含时间戳，但设计上能有效处理时间回退场景，不会因时间回退导致ID冲突
- **支持离线运行**：无需依赖中心化的ID生成服务，可在完全离线的局域网环境中正常工作
- **跨环境一致性**：无论是云服务还是本地化部署，使用统一的ID方案，简化系统设计和维护
- **数据库兼容性**：在不同部署环境可能使用不同数据库的情况下，UUID的跨数据库兼容性优势明显

因此，UUIDv7成为我们项目的最佳选择，既满足了分布式系统的全局唯一性要求，又解决了局域网环境的特殊问题。

## 2. UUID版本对比

| 版本 | 生成方式 | 特点 | 适用场景 |
|------|---------|------|----------|
| v1 | 基于时间戳和MAC地址 | 有序，可预测MAC地址 | 需要有序性但可接受MAC地址暴露 |
| v4 | 随机生成 | 完全随机，不可预测 | 安全性要求高，不需要有序性 |
| v7 | 基于时间戳和随机数 | 有序，包含时间信息 | 既需要有序性又需要安全性 |

## 3. UUIDv7 vs 雪花ID

### 3.1 UUIDv7优势

- **全局唯一性**：在任何网络环境下都能保证唯一性
- **无需中心化服务**：不需要像雪花ID那样依赖中心化的ID生成服务
- **时间有序**：包含时间戳，保证了插入顺序，有利于数据库索引性能
- **无时钟回拨问题**：不存在雪花ID的时钟回拨风险
- **跨数据库兼容性**：所有主流数据库都原生支持UUID类型
- **标准化**：遵循RFC 9562标准

### 3.2 雪花ID优势

- **长度更短**：通常为64位，存储占用更小
- **生成性能更高**：纯内存操作，无网络依赖
- **顺序性更好**：严格递增，有利于某些特定场景

### 3.3 适用场景选择

| 场景 | 推荐方案 | 理由 |
|------|---------|------|
| 分布式系统，跨网络环境 | UUIDv7 | 全局唯一性，无中心化依赖 |
| 高并发场景 | 雪花ID | 生成性能更高 |
| 局域网内部系统 | 雪花ID | 可预测性更好，性能更高 |
| 需要跨数据库迁移的系统 | UUIDv7 | 跨数据库兼容性更好 |
| 安全性要求高的系统 | UUIDv7 | 无MAC地址暴露，随机性更好 |

## 4. 项目实现

### 4.1 UUID生成器

项目使用`UUIDPrimaryKeyGenerator`类实现UUIDv7的生成：

```java
@Component
public class UUIDPrimaryKeyGenerator implements PrimaryKeyGenerator {
    @Override
    public Serializable getPrimaryKey() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
```

**说明**：
- 使用`UuidCreator`库生成符合RFC 9562标准的UUIDv7
- 生成的UUID包含时间戳信息，保证了插入顺序
- 通过`@Component`注解实现Spring Boot自动注入

### 4.2 实体类使用

BaseEntity类中使用UUID作为主键：

```java
@Data
public abstract class BaseEntity {
    @Column(primaryKey = true, primaryKeyGenerator = UUIDPrimaryKeyGenerator.class)
    private UUID id;
    
    // 其他字段...
}
```

**说明**：
- 使用`UUID`类型作为Java端字段类型
- 通过`@Column`注解指定主键和主键生成器
- 所有实体类通过继承BaseEntity获得UUID主键功能

## 5. 数据库字段类型

### 5.1 各数据库UUID字段类型

| 数据库 | 字段类型 | 存储大小 | 备注 |
|--------|---------|---------|------|
| PostgreSQL | `UUID` | 16字节 | 原生支持，推荐使用 |
| MySQL | `UUID` 或 `VARCHAR(36)` | 16字节 或 36字节 | MySQL 8.0+推荐使用`UUID`类型 |
| Oracle | `RAW(16)` 或 `VARCHAR2(36)` | 16字节 或 36字节 | `RAW(16)`更节省空间 |
| SQL Server | `UNIQUEIDENTIFIER` | 16字节 | 原生支持 |

### 5.2 索引优化

- **UUIDv7优势**：由于包含时间戳，插入顺序接近自增ID，索引碎片化程度低
- **索引类型**：推荐使用B-tree索引
- **查询优化**：对于UUID字段的查询，确保使用绑定变量以充分利用索引

## 6. 最佳实践

### 6.1 使用规范

1. **统一使用UUIDv7**：项目中所有ID字段统一使用UUIDv7
2. **Java类型**：使用`java.util.UUID`类型映射数据库UUID字段
3. **序列化**：JSON序列化时默认使用标准UUID格式（带连字符）
4. **传输**：API传输中保持UUID格式一致性

### 6.2 性能优化

- **批量插入**：UUIDv7的有序性使其在批量插入时性能接近自增ID
- **索引维护**：减少索引碎片化，降低数据库维护成本
- **存储优化**：使用数据库原生UUID类型，避免使用VARCHAR存储

### 6.3 代码示例

#### 6.3.1 实体类定义

```java
@Data
@Table("user")
public class User extends BaseEntity {
    private String username;
    private String email;
    // 其他字段...
}
```

#### 6.3.2 主键生成器配置

```java
// 已在BaseEntity中配置
@Column(primaryKey = true, primaryKeyGenerator = UUIDPrimaryKeyGenerator.class)
private UUID id;
```

## 7. 依赖配置

项目使用`uuid-creator`库生成UUIDv7：

```xml
<dependency>
    <groupId>com.github.f4b6a3</groupId>
    <artifactId>uuid-creator</artifactId>
    <version>5.1.0</version>
</dependency>
```

## 8. 与easy-query集成

项目通过实现`PrimaryKeyGenerator`接口与easy-query ORM框架集成：

1. 实现`UUIDPrimaryKeyGenerator`类
2. 使用`@Component`注解使其成为Spring Bean
3. 在实体类中通过`@Column`注解指定使用该生成器

## 9. 总结

项目选择UUIDv7作为主键方案，兼顾了：

- **全局唯一性**：适用于分布式系统
- **时间有序性**：有利于数据库性能
- **无中心化依赖**：简化系统架构
- **跨数据库兼容性**：便于系统迁移和多数据库支持

通过统一的UUID字段规范，提高了系统的可维护性、可扩展性和性能表现。