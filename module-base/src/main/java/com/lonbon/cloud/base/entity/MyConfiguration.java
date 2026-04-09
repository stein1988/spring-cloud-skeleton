package com.lonbon.cloud.base.entity;

import com.easy.query.core.bootstrapper.StarterConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MyConfiguration {
    @Bean("MyStarterConfigurer")
    @Primary
    public StarterConfigurer starterConfigurer() {
        return new MyStarterConfigurer();
    }
}