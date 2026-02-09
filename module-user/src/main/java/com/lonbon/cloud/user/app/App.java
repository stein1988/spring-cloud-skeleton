package com.lonbon.cloud.user.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.lonbon.cloud.user")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
