package com.lonbon.cloud.user.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@EnableDiscoveryClient
@ComponentScan(basePackages = "com.lonbon.cloud.user")
public class App {
    static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
