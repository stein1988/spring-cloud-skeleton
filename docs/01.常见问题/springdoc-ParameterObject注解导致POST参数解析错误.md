# Springdoc ParameterObject 注解导致 POST 参数解析错误问题

## 问题描述
在使用 Springdoc 生成 API 文档时，对于 `@PostMapping` 接口，DTO 类上添加了 `@ParameterObject` 注解后，Springdoc 会将请求参数解析为 URL 查询参数（如 `/api/auth/login?username=&password_cipher=&signature=`），而不是期望的 POST 请求体参数。

## 环境信息
- **时间**：2026.03.24
- **Spring Boot 版本**：3.x
- **Springdoc 版本**：2.x

## 代码示例

### Controller 代码
```java
@PostMapping("/login")
@Operation(summary = "登录", description = "用户名密码登陆")
public Response<LoginResponse> login(@RequestBody @Validated @NotNull LoginRequest request) {
    return Response.success(authService.login(request));
}
```

### DTO 代码
```java
@ParameterObject
@Data
public class LoginRequest {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码密文")
    private String password_cipher;

    @Schema(description = "签名")
    private String signature;
}
```

## 问题原因
`@ParameterObject` 注解会覆盖 `@RequestBody` 注解的效果，导致 Springdoc 认为参数应该通过 URL 查询字符串传递，而不是通过请求体。

## 排查步骤

1. **检查注解使用**：
   - 发现 DTO 类上添加了 `@ParameterObject` 注解
   - Controller 方法参数上添加了 `@RequestBody` 注解

2. **分析 Springdoc 文档生成**：
   - 访问 API 文档页面
   - 查看 `/api/auth/login` 接口的参数格式
   - 发现参数被解析为 URL 查询参数而不是请求体参数

## 已尝试解决方案

1. **移除 DTO 类上的 @ParameterObject 注解**：
   - 这是最直接的解决方案
   - 移除后 Springdoc 会正确解析 `@RequestBody` 注解

2. **使用 @Schema 注解替代 @ParameterObject**：
   - 对于需要在文档中展示的字段，使用 `@Schema` 注解即可
   - `@ParameterObject` 主要用于查询参数对象

## 最终解决方案

移除 DTO 类上的 `@ParameterObject` 注解，只保留 `@Data` 注解：

```java
@Data
public class LoginRequest {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码密文")
    private String password_cipher;

    @Schema(description = "签名")
    private String signature;
}
```

## 相关文档

- [Springdoc 官方文档](https://springdoc.org/)
- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)