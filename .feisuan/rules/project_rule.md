
# 开发规范指南
为保证代码质量、可维护性、安全性与可扩展性，请在开发过程中严格遵循以下规范。

## 一、项目基本信息

- **作者**：stein
- **用户工作目录**：`D:\Java\spring-cloud-skeleton`
- **开发环境**：Windows 10
- **当前时间**：2026-04-20 18:41:41

## 二、技术栈要求

- **主框架**：Spring Boot 4.0.5
- **语言版本**：Java 25
- **构建工具**：Maven
- **核心依赖**：
  - `spring-boot-starter-web`
  - `spring-boot-starter-webflux` (部分模块)
  - `sql-springboot4-starter` (Easy-Query)
  - `lombok`
  - `hutool-crypto`
  - `sa-token-spring-boot4-starter` (权限认证)
  - `mapstruct-plus-spring-boot-starter` (对象转换)
  - `springdoc-openapi-starter-webmvc-ui` (API文档)

## 三、项目目录结构

本项目采用多模块 Maven 结构，遵循分层领域驱动设计（DDD）思想。

```text
spring-cloud-skeleton
├── docs                          # 项目文档目录
│   ├── 01.常见问题
│   ├── 02.服务架构
│   ├── 03.数据库规范
│   └── ...
├── module-base                   # 基础模块 (公共依赖、工具类、基础实体)
│   └── src/main/java/com/lonbon/cloud/base
│       ├── dto                   # 通用数据传输对象
│       ├── entity                # 基础实体类
│       ├── exception             # 全局异常定义
│       ├── repository            # 基础仓储接口
│       ├── response              # 通用响应封装
│       ├── satoken               # 权限认证配置
│       ├── service               # 基础服务接口
│       └── spring                # Spring 配置类
├── module-demo                   # 演示模块
│   └── src/main/java/com/lonbon/cloud/demo
└── module-user                   # 用户业务模块 (标准DDD分层)
    └── src/main/java/com/lonbon/cloud/user
        ├── api                   # 接口层 (对外暴露)
        │   └── controller        # REST 控制器
        ├── app                   # 应用层 (编排)
        ├── application           # 应用服务 (具体实现)
        │   └── service
        ├── domain                # 领域层 (核心业务)
        │   ├── dto               # 领域 DTO
        │   ├── entity            # 领域实体
        │   ├── filter            # 查询过滤条件
        │   ├── repository        # 仓储接口
        │   ├── service           # 领域服务
        │   └── value_object      # 值对象
        └── infrastructure        # 基础设施层 (技术实现)
            ├── config            # 配置类
            └── repository        # 仓储实现 (数据库操作)
```

## 四、分层架构规范

| 层级           | 职责说明                                       | 开发约束与注意事项                                                     |
|----------------|------------------------------------------------|------------------------------------------------------------------------|
| **Controller** | 处理 HTTP 请求与响应，定义 API 接口             | 不得直接访问数据库；仅做参数校验、调用 Service、封装返回结果          |
| **Application**| 应用服务，负责业务流程编排与事务管理             | 协调多个 Domain Service 和 Infrastructure；处理跨领域事务             |
| **Domain**     | 核心业务逻辑，包含 Entity、Value Object 等      | 保持纯粹，不依赖具体框架；封装核心业务规则                             |
| **Repository** | 数据访问接口 (Domain层) 与 实现 (Infra层)      | 使用 Easy-Query 进行数据库操作；实现类位于 `infrastructure.repository` |

### 接口与实现分离

- 基础模块：接口与实现类需放在接口所在包下的 `impl` 子包中。
- 业务模块（如 module-user）：遵循 DDD 分层，接口定义在 `domain`，实现在 `infrastructure`。

## 五、安全与性能规范

### 输入校验

- 使用 `@Valid` 与 JSR-303 校验注解（如 `@NotBlank`, `@Size` 等）。
  - 注意：Spring Boot 4.x 中校验注解位于 `jakarta.validation.constraints.*`。

### 权限认证

- 使用 **Sa-Token** 进行权限认证。
- 敏感接口需添加 Sa-Token 注解（如 `@SaCheckLogin`, `@SaCheckRole`）。
- 配置 API 参数签名校验 (`sa-token-sign`) 以增强接口安全性。

### 事务管理

- `@Transactional` 注解仅用于 **Application 层** 或 **Service 层** 方法。
- 避免在循环中频繁提交事务，影响性能。

## 六、代码风格规范

### 命名规范

| 类型       | 命名方式             | 示例                  |
|------------|----------------------|-----------------------|
| 类名       | UpperCamelCase       | `UserServiceImpl`     |
| 方法/变量  | lowerCamelCase       | `saveUser()`          |
| 常量       | UPPER_SNAKE_CASE     | `MAX_LOGIN_ATTEMPTS`  |

### 注释规范

- **语言要求**：请使用中文（简体）编写注释，确保团队内部理解一致。
- 所有类、方法、字段需添加 **Javadoc** 注释。

### 类型命名规范（阿里巴巴风格）

| 后缀      | 用途说明                     | 示例         |
|-----------|------------------------------|--------------|
| DTO       | 数据传输对象                 | `UserDTO`    |
| Entity    | 数据库实体对象 / 领域实体    | `UserEntity` |
| VO        | 视图展示对象                 | `UserVO`     |
| Query/Filter| 查询参数封装对象             | `UserQuery`  |

### 实体类简化工具

- 使用 Lombok 注解替代手动编写 getter/setter/构造方法：
  - `@Data`
  - `@NoArgsConstructor`
  - `@AllArgsConstructor`

### 对象映射

- 使用 **MapStruct-Plus** 进行对象映射（Entity <-> DTO）。
- 配置了 `nullValuePropertyMappingStrategy=IGNORE`，避免源对象 null 值覆盖目标对象。

## 七、扩展性与日志规范

### 日志记录

- 使用 `@Slf4j` 注解代替 `System.out.println`。
- 在 `application.yml` 中配置 `logging.level.com.easy.query.core: DEBUG` 以便开发时调试 SQL。

### API 文档

- 使用 **SpringDoc OpenAPI** 生成接口文档。
- 访问路径：`/swagger-ui.html`。
- 需在 Controller 中添加 `@Tag` 注解进行分组。

## 八、编码原则总结

| 原则       | 说明                                       |
|------------|--------------------------------------------|
| **SOLID**  | 高内聚、低耦合，增强可维护性与可扩展性     |
| **DRY**    | 避免重复代码，提高复用性                   |
| **KISS**   | 保持代码简洁易懂                           |
| **YAGNI**  | 不实现当前不需要的功能                     |
| **OWASP**  | 防范常见安全漏洞，如 SQL 注入、XSS 等      |
