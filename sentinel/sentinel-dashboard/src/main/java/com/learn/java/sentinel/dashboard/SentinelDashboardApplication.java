package com.learn.java.sentinel.dashboard;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

/**
 * -Dcsp.sentinel.dashboard.server=127.0.0.1:8888 -Dserver.name=sentinel-dashboard-app -Dserver.port=12345
 *
 * sentinel dashboard
 */
@SpringBootApplication
public class SentinelDashboardApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(SentinelDashboardApplication.class,args);
    }
}
