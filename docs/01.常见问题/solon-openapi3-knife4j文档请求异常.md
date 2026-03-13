# Solon OpenAPI3 Knife4j 文档请求异常问题

## 问题描述
访问 `http://localhost:8080/doc.html` 时，提示 "Knife4j文档请求异常"。

## 环境信息
- **时间**：2026.02.06
- **Solon 版本**：3.9.0

## 配置信息

### Knife4j 配置
```yaml
knife4j.enable: true
knife4j.basic.enable: true
knife4j.basic.username: admin
knife4j.basic.password: 123456
knife4j.setting.enableOpenApi: true
knife4j.setting.enableSwaggerModels: true
knife4j.setting.enableFooter: true
```

### Solon 资源映射配置
```yaml
# Solon 资源映射：放行Knife4j所有静态资源和接口
solon.resource:
  # 映射Knife4j文档主页（核心，缺失则/doc.html无法访问）
  mapping: /doc.html=classpath:/META-INF/resources/doc.html
  # 批量映射静态资源和OpenAPI3接口（CSS/JS/图标/文档数据接口）
  mappings:
    /webjars/**=classpath:/META-INF/resources/webjars/
    /v3/api-docs/**=classpath:/META-INF/resources/
    /openapi/**=classpath:/META-INF/resources/
    /knife4j/**=classpath:/META-INF/resources/
```

## 排查步骤

1. **查看官方文档**：
   - [Knife4j 官方异常排查文档](https://doc.xiaominfo.com/docs/faq/knife4j-exception)

2. **浏览器开发者工具分析**：
   - 打开网页 F12 开发者工具
   - 过滤网络请求 XHR
   - 发现 doc.html 返回：
     ```
     Please ensure that the static resources of Knife4j are released to avoid being intercepted by the back-end security framework!. Enable it to continue.
     ```

## 已尝试解决方案

确保 Solon 资源映射配置正确，特别是以下几点：

1. **必须映射文档主页**：
   ```yaml
   mapping: /doc.html=classpath:/META-INF/resources/doc.html
   ```

2. **必须映射所有静态资源**：
   ```yaml
   mappings:
     /webjars/**=classpath:/META-INF/resources/webjars/
     /v3/api-docs/**=classpath:/META-INF/resources/
     /openapi/**=classpath:/META-INF/resources/
     /knife4j/**=classpath:/META-INF/resources/
   ```

## 未尝试解决方案

1. **检查安全框架配置**：
   - 确保安全框架（如 Shiro、Spring Security 等）不会拦截这些路径
   - 如果使用了安全框架，需要在安全配置中放行这些路径

## 最终解决方案

查看官方文档，发现需要在配置中增加solon.docs相关配置

   ```yaml
   solon.docs:
   routes:
      - id: userApi
         groupName: "user服务接口"
         info:
         title: "在线文档"
         description: "在线API文档"
         termsOfService: "https://gitee.com/noear/solon"
         contact:
            name: "demo"
            url: "https://gitee.com/noear/solon"
            email: "demo@foxmail.com"
         version: "1.0"
         schemes:
         - "HTTP"
         - "HTTPS"
         globalResponseInData: true
         globalResult: "com.lonbon.cloud.common.utils.Response"
         apis:
         - basePackage: "com.lonbon.cloud.user"
   ```

## 相关文档

- [solon 官方文档|教程|Solon Docs 开发|文档摘要的配置与构建](https://solon.noear.org/article/796)

