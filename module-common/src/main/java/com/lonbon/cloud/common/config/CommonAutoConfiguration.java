package com.lonbon.cloud.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 公共模块自动配置类
 * 用于扫描公共模块的组件，包括异常处理器
 */
@Configuration
@ComponentScan("com.lonbon.cloud.common")
public class CommonAutoConfiguration {
}