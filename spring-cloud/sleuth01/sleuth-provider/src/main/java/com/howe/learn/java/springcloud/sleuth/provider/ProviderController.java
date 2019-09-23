package com.howe.learn.java.springcloud.sleuth.provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController {

    private static final Logger log = LoggerFactory.getLogger(ProviderController.class);

    @RequestMapping("/sayHello")
    public String hello(String name) {
        log.info("server received. 参数:{}", name);
        String result = "hello, " + name;
        log.info("server sent. 结果:{}", result);
        return result;
    }
}
