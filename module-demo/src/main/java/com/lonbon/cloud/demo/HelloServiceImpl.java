package com.lonbon.cloud.demo;

import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloService{
    @Override
    public String hello(String name) {
        return String.format("Hello %s!", name);
    }
}