package com.lonbon.cloud.base.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 基础模块自动配置类
 * 用于扫描 base 模块的组件，使其能被上层模块自动注入
 */
@Configuration
@ComponentScan("com.lonbon.cloud.base") // base 模块的根包
public class BaseAutoConfiguration {
}
