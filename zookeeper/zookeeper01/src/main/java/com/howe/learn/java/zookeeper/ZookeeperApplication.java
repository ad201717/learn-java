package com.howe.learn.java.zookeeper;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryUntilElapsed;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ZookeeperApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZookeeperApplication.class, args);
    }

    @Bean
    public CuratorFramework zookeeperClient() {
        CuratorFramework zookeeperClient = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 3000, 3000, new RetryNTimes(100, 3000));
        zookeeperClient.start();
        return zookeeperClient;
    }

}
