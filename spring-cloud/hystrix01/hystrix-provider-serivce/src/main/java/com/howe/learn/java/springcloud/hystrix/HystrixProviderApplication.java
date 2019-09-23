package com.howe.learn.java.springcloud.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HystrixProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixProviderApplication.class, args);
    }
}
