package com.learn.java.sentinel.dashboard;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

/**
 * sentinel dashboard
 */
@SpringBootApplication
public class SentinelDashboardApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(SentinelDashboardApplication.class,args);
    }
}
