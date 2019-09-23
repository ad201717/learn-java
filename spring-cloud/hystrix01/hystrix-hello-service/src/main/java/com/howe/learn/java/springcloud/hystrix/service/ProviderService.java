package com.howe.learn.java.springcloud.hystrix.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "sc-provider-service")
public interface ProviderService {

    @RequestMapping(value = "/getDashboard", method = RequestMethod.GET)
    public List<String> getProviderData();
}
