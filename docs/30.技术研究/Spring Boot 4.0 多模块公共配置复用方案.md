# Spring Boot 4\.0 多模块公共配置复用方案

针对**Spring Boot 4\.0** 框架，完全支持将公共配置写在 base module 的 yaml 文件中，本文提供**完全适配4\.0新规范、无废弃API风险**的两套实现方案，优先推荐官方标准方案。

---

## 核心前提说明（Spring Boot 4\.0 关键变更）

1. **废弃API避坑**：旧版 `org\.springframework\.boot\.env\.EnvironmentPostProcessor` 已在4\.0标记为**待删除（forRemoval=true）**，计划4\.2\.0正式移除，包路径已迁移至`org\.springframework\.boot`，同时**`spring\.factories`****完全移除了  自动配置注册机制**，所有自动配置必须使用新标准 `META\-INF/spring/org\.springframework\.boot\.autoconfigure\.AutoConfiguration\.imports` 注册。

2. **官方推荐标准**：Spring Boot 2\.4\+ 就主推 `spring\.config\.import` 导入配置，4\.0完全兼容且无兼容风险，是多模块配置复用的首选方案。

---

## 方案一：Spring Boot 4\.0 官方标准方案（首选推荐）

### 核心优势

零Java代码、原生支持、无废弃API风险、配置优先级清晰、易维护，团队内部多模块场景首选。

### 实现步骤

#### 1\. base module 编写公共配置文件

在base模块的resources目录下，创建独立命名的配置文件（**`application\.yml`****禁止使用默认的 ，避免与业务模块同名文件冲突**）。

路径：`base/src/main/resources/application\-base\.yml`

```yaml
# 这里写所有模块通用的默认配置，完全写在文件中，无需硬编码
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: postgres
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  jackson:
    default-property-inclusion: non_null
    time-zone: GMT+8

# 通用业务配置
myapp:
  default-timeout: 5000
  retry-count: 3
```

*`application\-base\-dev\.ymlapplication\-base\-prod\.yml`**多环境支持：可同步创建 /，会跟随业务模块激活的profile自动生效。*

#### 2\. 业务模块引入base依赖

在业务模块的 `pom\.xml` 中引入base模块：

```xml
<dependency>
    <groupId>com.yourcompany</groupId>
    <artifactId>base</artifactId>
    <version>${project.version}</version>
</dependency>
```

#### 3\. 业务模块一行配置导入公共配置

在业务模块自身的 `application\.yml` 中，通过 `spring\.config\.import` 导入base模块的配置，无需其他任何操作：

```yaml
spring:
  application:
    name: business-service
  config:
    # 核心：导入base模块中的公共配置
    # optional: 前缀表示即使配置文件不存在，也不会报错，提升兼容性
    import: "optional:classpath:application-base.yml"

# 业务模块特有配置，可覆盖base模块的同名配置
server:
  port: 8081
```

### 关键特性

- **优先级规则**：业务模块自身的配置 \&gt; 导入的base公共配置，同名key会自动覆盖，符合开发预期。

- **零代码侵入**：全程无需编写Java代码，纯配置实现，无兼容风险。

- **全场景兼容**：完美支持多环境profile、配置加密、AOT原生镜像等Spring Boot 4\.0新特性。

---

## 方案二：无侵入自动加载方案（框架级封装适用）

### 核心优势

业务模块**仅需引入base依赖，无需任何额外配置**，启动时自动加载base模块的公共配置，适合开发公共组件/底层框架。

### 实现步骤（完全适配Spring Boot 4\.0规范）

#### 1\. base module 编写公共配置文件

将配置文件放在 `META\-INF` 目录下，彻底避免与业务模块文件冲突。

路径：`base/src/main/resources/META\-INF/application\-base\-defaults\.yml`

```yaml
# 通用默认配置，与方案一的配置内容完全一致
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: postgres
    driver-class-name: org.postgresql.Driver
  jackson:
    default-property-inclusion: non_null
    time-zone: GMT+8

myapp:
  default-timeout: 5000
```

#### 2\. 编写配置加载监听器（替代废弃的EnvironmentPostProcessor）

使用Spring官方推荐的 `ApplicationListener\&lt;ApplicationEnvironmentPreparedEvent\&gt;` 实现配置加载，无废弃API风险，兼容Spring Boot 4\.0全版本。

路径：`base/src/main/java/com/yourcompany/base/config/BaseConfigLoadListener\.java`

```java
package com.yourcompany.base.config;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Spring Boot 4.0 兼容的公共配置自动加载监听器
 * 执行时机：应用环境准备完成，上下文刷新前，与旧版EnvironmentPostProcessor一致
 */
public class BaseConfigLoadListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private final YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
    private static final String CONFIG_PATH = "META-INF/application-base-defaults.yml";
    private static final String CONFIG_NAME = "base-module-default-config";

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        Resource configResource = new ClassPathResource(CONFIG_PATH);

        if (!configResource.exists()) {
            return;
        }

        try {
            // 加载yaml配置文件
            PropertySource<?> propertySource = yamlLoader.load(CONFIG_NAME, configResource).getFirst();
            // addLast：设置为最低优先级，业务模块的配置可无条件覆盖
            environment.getPropertySources().addLast(propertySource);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load base module default config", e);
        }
    }
}
```

#### 3\. 编写自动配置类，注册监听器

路径：`base/src/main/java/com/yourcompany/base/config/BaseModuleAutoConfiguration\.java`

```java
package com.yourcompany.base.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot 4.0 标准自动配置类
 * 必须使用@AutoConfiguration注解，替代旧版@Configuration
 */
@AutoConfiguration
public class BaseModuleAutoConfiguration {

    @Bean
    public ApplicationListener<ApplicationEnvironmentPreparedEvent> baseConfigLoadListener() {
        return new BaseConfigLoadListener();
    }

    // 可扩展：在这里注册@ConfigurationProperties配置绑定类，实现类型安全的配置注入
}
```

#### 4\. Spring Boot 4\.0 标准方式注册自动配置

在base模块中创建注册文件，**这是4\.0唯一支持的自动配置注册方式，禁止使用spring\.factories**。

路径：`base/src/main/resources/META\-INF/spring/org\.springframework\.boot\.autoconfigure\.AutoConfiguration\.imports`

```text
# 每行一个自动配置类的全限定名
com.yourcompany.base.config.BaseModuleAutoConfiguration
```

### 最终效果

业务模块**只需要在pom\.xml中引入base模块依赖**，无需在自身的application\.yml中添加任何配置，启动时会自动加载base模块的公共配置，且业务模块的同名配置会自动覆盖base的默认配置。

---

## 关键注意事项

1. **文件名避坑**：base模块的公共配置文件**禁止使用默认的application\.yml**，否则会被业务模块的同名文件覆盖，导致配置加载异常。

2. **优先级控制**：两种方案均默认设置公共配置为最低优先级，确保业务模块的个性化配置可以无条件覆盖，符合多模块开发的最佳实践。

3. **多环境兼容**：两套方案均完美支持多环境profile，可按环境拆分公共配置，跟随业务模块激活的profile自动生效。

4. **AOT原生镜像支持**：方案一原生支持Spring Boot 4\.0的AOT原生编译；方案二需在自动配置类中添加AOT相关适配，即可兼容原生镜像部署。

> （注：文档部分内容可能由 AI 生成）
