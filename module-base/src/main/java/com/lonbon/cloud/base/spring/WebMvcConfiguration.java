package com.lonbon.cloud.base.spring;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Web MVC 配置类，用于自动为控制器添加路径前缀
 * <p>
 * 功能：
 * 1. 扫描所有带有 @Controller 或 @RestController 注解的类
 * 2. 根据包名提取模块名，匹配正则表达式 com.lonbon.cloud.{module}.*
 * 3. 根据模块名和控制器上的 @RequestMapping 注解自动生成路径前缀
 * 4. 为控制器批量注册路径前缀
 * <p>
 * 路径前缀规则：
 * - 如果控制器的 @RequestMapping 中已包含版本号（如 /v2/users），则前缀为 /api/{module}/v2/users
 * - 如果控制器的 @RequestMapping 中未包含版本号（如 /users），则前缀为 /api/{module}/v1/users
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * Spring 应用上下文，用于扫描控制器 Bean
     */
    private final ApplicationContext applicationContext;

    /**
     * 构造函数
     *
     * @param applicationContext Spring 应用上下文
     */
    public WebMvcConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 配置路径匹配规则
     *
     * @param configurer 路径匹配配置器
     */
    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        // 1. 启动时扫描并构建「前缀 -> Controller类集合」的映射
        Map<String, Set<Class<?>>> prefixControllerMap = buildPrefixControllerMap();

        // 2. 遍历映射，批量注册前缀
        prefixControllerMap.forEach(
                (prefix, controllerClasses) -> configurer.addPathPrefix(prefix, controllerClasses::contains));
    }

    /**
     * 核心方法：扫描所有 Controller，按前缀分组
     *
     * @return 前缀到控制器类集合的映射
     */
    private @NonNull Map<String, Set<Class<?>>> buildPrefixControllerMap() {
        Map<String, Set<Class<?>>> result = new HashMap<>();

        // 1. 获取所有带 @RestController 或 @Controller 的 Bean
        Map<String, Object> controllerBeans = new HashMap<>();
        controllerBeans.putAll(applicationContext.getBeansWithAnnotation(RestController.class));
        controllerBeans.putAll(applicationContext.getBeansWithAnnotation(Controller.class));

        // 2. 遍历每个 Controller，计算它的前缀
        for (Object controllerBean : controllerBeans.values()) {
            // 关键：Spring 可能会生成代理类（如 CGLIB），需要获取原始类
            Class<?> controllerClass = ClassUtils.getUserClass(controllerBean);

            // 从类信息中提取前缀
            String prefix = extractPrefix(controllerClass);
            if (prefix == null) continue;

            // 3. 按前缀分组
            result.computeIfAbsent(prefix, k -> new HashSet<>()).add(controllerClass);
        }

        return result;
    }

    /**
     * 从控制器类中提取路径前缀
     *
     * @param controllerClass 控制器类
     * @return 路径前缀，如果无法提取则返回 null
     */
    private @Nullable String extractPrefix(@NonNull Class<?> controllerClass) {
        // 1. 从包名中提取模块名
        String packageName = controllerClass.getPackage().getName();
        // 正则解释：
        // com\.lonbon\.cloud\.   -> 匹配固定前缀 "com.lonbon.cloud."
        // (\w+)                   -> 捕获我们需要的 module 名（如 "user"、"order"）
        // (\..*)?                 -> 匹配后面的 ".application.controller" 等剩余部分（可选）
        Pattern pattern = Pattern.compile("com\\.lonbon\\.cloud\\.(\\w+)(\\..*)?");
        Matcher matcher = pattern.matcher(packageName);
        if (!matcher.find()) {
            return null;
        }
        String module = matcher.group(1);

        // 2. 检查 @RequestMapping 的 path 是否包含版本号
        RequestMapping requestMapping = controllerClass.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            // 获取 @RequestMapping 的路径值
            String[] paths = requestMapping.path().length > 0 ? requestMapping.path() : requestMapping.value();
            if (paths.length > 0) {
                String path = paths[0];
                // 匹配版本号格式，如 /v1 或 /v1.0
                Pattern versionPattern = Pattern.compile(".*/v(\\d+(\\.\\d+)?).*");
                Matcher versionMatcher = versionPattern.matcher(path);
                if (versionMatcher.find()) {
                    // 有版本号：只加 /api/{module}
                    return String.format("/api/%s", module);
                }
            }
        }

        // 3. 无版本号：默认加 /api/{module}/v1
        return String.format("/api/%s/v1", module);
    }
}
