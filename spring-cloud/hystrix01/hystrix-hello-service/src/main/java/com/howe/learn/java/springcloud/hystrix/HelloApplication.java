package com.howe.learn.java.springcloud.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker
public class HelloApplication {

//    /**
//     * 低版本直接启动即可使用 http://ip:port/hystrix.stream 查看监控信息
//     * 高版本需要添加本方法方可使用 http://ip:port/hystix.stream 查看监控信息
//     *
//     * @return
//     */
//    @Bean
//    public ServletRegistrationBean getServlet() {
//        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
//        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
//        registrationBean.setLoadOnStartup(1);
//        registrationBean.addUrlMappings("/hystrix.stream");
//        registrationBean.setName("HystrixMetricsStreamServlet");
//        return registrationBean;
//    }

    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }
}
