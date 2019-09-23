package com.howe.learn.java.springcloud.pinpoint.serviceb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkySecondController {

    @GetMapping("/getSendInfo")
    public String getSendInfo(@RequestParam("serviceName")String serviceName) {
        return serviceName + " -----> " + "service-b";
    }

}
