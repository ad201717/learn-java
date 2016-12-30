package com.learn.howe.springBoot.simpleStub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @Author Karl
 * @Date 2016/12/28 15:18
 */
@EnableAutoConfiguration
@EnableWebMvc
@SpringBootApplication
@ComponentScan("com.learn.howe.springBoot.simpleStub")
public class Main implements EmbeddedServletContainerCustomizer {

    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
        configurableEmbeddedServletContainer.setPort(9090);
    }

}
