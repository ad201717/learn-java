package com.howe.learn.java.springcloud.hystrix.controller;

import com.howe.learn.java.springcloud.hystrix.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private ProviderService providerService;

    @RequestMapping("getProviderData")
    public List<String> getProviderData() {
        return providerService.getProviderData();
    }
}
