package com.lonbon.cloud.base.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 基础模块自动配置类
 * 用于扫描 base 模块的组件，使其能被上层模块自动注入
 */
@AutoConfiguration
@ComponentScan("com.lonbon.cloud.base")
public class BaseAutoConfiguration {
}
