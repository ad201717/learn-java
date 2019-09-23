package com.howe.learn.java.springcloud.pinpoint.servicea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkyController {

    @Autowired
    private SkyFeignService skyFeignService;

    @GetMapping("/getInfo")
    public String getInfo() {
        return skyFeignService.getSendInfo("service-a");
    }

}
