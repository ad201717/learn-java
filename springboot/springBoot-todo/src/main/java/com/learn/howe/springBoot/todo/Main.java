package com.learn.howe.springBoot.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

/**
 * @Author Karl
 * @Date 2016/12/28 15:18
 */
@EnableAutoConfiguration
@EnableWebMvc
@SpringBootApplication
@ComponentScan("com.learn.howe.springBoot.todo")
@ConditionalOnClass(SpringTemplateEngine.class)
public class Main extends WebMvcConfigurerAdapter implements EmbeddedServletContainerCustomizer {
    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
        configurableEmbeddedServletContainer.setPort(9090);
    }

    @Bean
    public ViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(springTemplateEngine);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/out/**").addResourceLocations("file:D:/static/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:static/");
        super.addResourceHandlers(registry);
    }
}
