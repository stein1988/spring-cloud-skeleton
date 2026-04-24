---
name: "entity-crud-generator"
description: "Generates Repository, Service, Controller code from entity. Detects closure entity automatically. Invoke when user asks to create CRUD code for an entity or complete entity-related code."
---

# Entity CRUD 代码生成器

此技能根据项目的 DDD 架构模式，自动从实体类生成 Repository、Service、Controller 和 DTO 代码。

## 使用场景

在以下情况调用此技能：

- 用户要求为实体创建 Repository、Service、Controller
- 用户要求完善实体的相关代码
- 用户要求"生成 CRUD 代码"或"创建相关代码"
- 用户要求"完善"实体的相关代码

## 核心逻辑

### 步骤 1: 检测闭包实体

**关键**: 在生成代码之前，检查实体是否有对应的闭包实体类：

1. 在同一实体包中查找 `<实体名称>Closure.java`
2. 如果找到，说明该实体实现了层级/闭包表模式
3. 如果没找到，则按普通实体处理

### 步骤 2: 实体分析

读取实体类并识别：

- 实体名称和包路径
- 字段及其类型
- 是否实现了 `ClosureAvailable<T>` 接口
- 是否有父子导航属性

### 步骤 3: 代码生成

#### A. 未找到闭包实体（普通实体）

需要生成：

1. **Repository 接口**
    - 路径: `domain/repository/<实体名称>Repository.java`
    - 继承: `Repository<Entity, EntityProxy>`

2. **Repository 实现类**
    - 路径: `infrastructure/repository/<实体名称>RepositoryImpl.java`
    - 继承: `EasyQueryRepository<Entity, EntityProxy, EntityProxy.EntityProxyFetcher>`
    - 实现对应的 Repository 接口

3. **Service 接口**
    - 路径: `domain/service/<实体名称>Service.java`
    - 继承: `Service<Entity, EntityProxy>`

4. **Service 实现类**
    - 路径: `application/service/<实体名称>ServiceImpl.java`
    - 继承: `SimpleEntityService<Entity, EntityProxy>`
    - 构造方法: `(Converter converter, EntityRepository repository)`

5. **DTO 数据传输对象**
    - CreateDTO: 路径 `domain/dto/<实体名称>CreateDTO.java`
    - UpdateDTO: 路径 `domain/dto/<实体名称>UpdateDTO.java`
    - 使用 `@AutoMapper(target = Entity.class, reverseConvertGenerate = false)`
    - 为必填字段添加 `@NotEmpty` 验证

6. **Controller 控制器**
    - 路径: `api/controller/<实体名称>Controller.java`
    - 添加注解 `@RestController`、`@RequestMapping`、`@Tag`
    - 提供标准 CRUD 接口:
        - POST `/api/<实体>` - 创建
        - POST `/api/<实体>/{id}/delete` - 删除
        - POST `/api/<实体>/{id}/update` - 更新
        - GET `/api/<实体>/{id}` - 根据 ID 获取
        - GET `/api/<实体>` - 获取所有

#### B. 找到闭包实体（层级实体）

**重要**: 使用接口组合模式（`ClosureOperations`），而非继承 `ClosureEntityService`。

需要生成：

1. **闭包实体检查**
    - 确认闭包实体实现了 `Closure` 接口
    - 确认主实体实现了 `ClosureAvailable<闭包实体>` 接口

2. **闭包 Repository 接口**
    - 路径: `domain/repository/<实体名称>ClosureRepository.java`
    - 继承: `Repository<闭包实体, 闭包实体Proxy>`

3. **闭包 Repository 实现类**
    - 路径: `infrastructure/repository/<实体名称>ClosureRepositoryImpl.java`
    - 继承: `EasyQueryRepository<闭包实体, 闭包实体Proxy, 闭包实体Proxy.闭包实体ProxyFetcher>`

4. **闭包过滤器（如果不存在）**
    - 路径: `domain/filter/<实体名称>ClosureFilter.java`
    - 实现 `NavigateExtraFilterStrategy`
    - 过滤条件: `distance > 0`

5. **主实体 Repository 接口**
    - 路径: `domain/repository/<实体名称>Repository.java`
    - 继承: `Repository<Entity, EntityProxy>`

6. **主实体 Repository 实现类**
    - 路径: `infrastructure/repository/<实体名称>RepositoryImpl.java`
    - 继承: `EasyQueryRepository<Entity, EntityProxy, EntityProxy.EntityProxyFetcher>`

7. **Service 接口**
    - 路径: `domain/service/<实体名称>Service.java`
    - 继承: `ClosureService<Entity, EntityProxy, 闭包实体, 闭包实体Proxy>`

8. **Service 实现类**
    - 路径: `application/service/<实体名称>ServiceImpl.java`
    - **关键**: 使用接口组合模式
        - 继承: `SimpleEntityService<Entity, EntityProxy>`
        - 实现: `ClosureOperations<Entity, EntityProxy, 闭包实体, 闭包实体Proxy>`
    - 实现以下方法:
      ```java
      @Service
      @Transactional(rollbackFor = Exception.class)
      public class <实体>ServiceImpl
              extends SimpleEntityService<<实体>, <实体>Proxy>
              implements <实体>Service,
                         ClosureOperations<<实体>, <实体>Proxy, <闭包实体>, <闭包实体>Proxy> {

          private final <闭包实体>Repository closureRepository;

          public <实体>ServiceImpl(
                  Converter converter, <实体>Repository repository, <闭包实体>Repository closureRepository) {
              super(converter, repository, <实体>.class);
              this.closureRepository = closureRepository;
          }

          @Override
          public Repository<<闭包实体>, <闭包实体>Proxy> getClosureRepository() {
              return closureRepository;
          }

          @Override
          public Repository<<实体>, <实体>Proxy> getEntityRepository() {
              return repository;
          }

          @Override
          public <闭包实体> createClosure(UUID ancestorId, UUID descendantId, Integer distance) {
              return new <闭包实体>(ancestorId, descendantId, distance);
          }

          @Override
          public <实体> createBaseEntity(Object createDto) {
              return super.createEntity(createDto);
          }

          @Override
          public SQLActionExpression2<IncludeContext, <实体>Proxy> navigate() {
              return (c, t) -> c.query(t.ancestors());
          }

          @Override
          public SQLFuncExpression1<<实体>Proxy, SQLSelectExpression> setColumnParentId() {
              return <实体>Proxy::parentId;
          }
      }
      ```

9. **DTO 数据传输对象**
    - CreateDTO 必须包含 `parentId` 字段 (UUID 类型) - 用于创建时指定父节点
    - UpdateDTO **不应该**包含 `parentId` 字段 - 修改父节点应使用 move 接口
    - 路径: `domain/dto/<实体名称>CreateDTO.java`
    - 路径: `domain/dto/<实体名称>UpdateDTO.java`

10. **Controller 控制器**
    - 路径: `api/controller/<实体名称>Controller.java`
    - 标准 CRUD 接口
    - **添加闭包专用接口**:
        - GET `/api/<实体>/{id}/children` - 获取直接子节点
        - GET `/api/<实体>/{id}/descendants` - 获取所有后代节点
        - GET `/api/<实体>/{id}/parent` - 获取直接父节点
        - GET `/api/<实体>/{id}/ancestors` - 获取所有祖先节点
        - POST `/api/<实体>/{id}/move` - 移动节点到新父节点
        - GET `/api/<实体>/{id}/tree` - 获取树结构

## 代码生成指南

### 实体要求

对于闭包实体，确保主实体有：

- `UUID parentId` 字段
- `List<闭包实体> ancestors` 带有 `@Navigate` 注解
- `List<闭包实体> descendants` 带有 `@Navigate` 注解
- 实现了 `ClosureAvailable<闭包实体>` 接口
- 使用了 `@FieldNameConstants` 注解

对于闭包实体本身：

- 实现了 `Closure` 接口
- 包含字段: `ancestorId`、`descendantId`、`distance`

### 包结构

遵循现有项目结构:

```
module-user/src/main/java/com/lonbon/cloud/user/
├── domain/
│   ├── entity/          # 实体类
│   ├── repository/      # Repository 接口
│   ├── service/         # Service 接口
│   ├── dto/             # 数据传输对象
│   └── filter/          # 闭包过滤器（如果需要）
├── infrastructure/
│   └── repository/      # Repository 实现类
└── api/
    └── controller/      # REST 控制器
```

### 命名规范

- Repository: `<实体名称>Repository`、`<实体名称>RepositoryImpl`
- Service: `<实体名称>Service`、`<实体名称>ServiceImpl`
- DTO: `<实体名称>CreateDTO`、`<实体名称>UpdateDTO`
- Controller: `<实体名称>Controller`
- 闭包: `<实体名称>Closure`、`<实体名称>ClosureFilter`

### 导入语句

根据需要使用以下导入:

```java
// Repository

import com.lonbon.cloud.base.repository.Repository;
import com.lonbon.cloud.base.repository.EasyQueryRepository;
import com.easy.query.api.proxy.client.EasyEntityQuery;

// Service
import com.lonbon.cloud.base.service.Service;
import com.lonbon.cloud.base.service.EntityService;
import com.lonbon.cloud.base.service.ClosureService;
import com.lonbon.cloud.base.service.ClosureOperations;
import com.lonbon.cloud.base.service.SimpleEntityService;
import io.github.linpeilie.Converter;

// 闭包
import com.lonbon.cloud.base.service.Closure;
import com.lonbon.cloud.base.service.ClosureAvailable;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.expression.lambda.SQLFuncExpression1;
import com.easy.query.core.proxy.SQLSelectExpression;
import com.easy.query.core.proxy.sql.include.IncludeContext;
```

## 使用示例

当用户说: "完善 Department 的相关代码"

1. 检查 entity 包中是否有 `DepartmentClosure.java`
2. 找到了，使用闭包模式
3. 生成所有闭包相关代码，如上所述
4. 在 Controller 中添加闭包专用 API 接口

当用户说: "创建 User 的 Repository 和 Service"

1. 检查是否有 `UserClosure.java`
2. 没找到，使用普通模式
3. 生成标准 Repository、Service、DTO、Controller

## 最佳实践

1. 在生成代码前先读取现有代码模式
2. 遵循项目的精确编码风格
3. 使用 Lombok 注解（`@Data`、`@Service` 等）
4. 添加 Swagger 注解（`@Operation`、`@Tag`）
5. 使用 `@Validated` 和 `@NotNull` 进行验证
6. 包含适当的 JavaDoc 注释
7. 使用 `Response<T>` 确保 API 响应一致
8. 始终为 Service 实现类添加 `@Transactional(rollbackFor = Exception.class)`
9. **闭包 Service 使用接口组合模式**：继承 `SimpleEntityService` + 实现 `ClosureOperations` 接口
