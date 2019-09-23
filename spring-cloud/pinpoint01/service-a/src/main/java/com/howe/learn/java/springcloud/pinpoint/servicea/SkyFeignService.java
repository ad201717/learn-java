package com.howe.learn.java.springcloud.pinpoint.servicea;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-b")
public interface SkyFeignService {

    @RequestMapping(value = "/getSendInfo", method = RequestMethod.GET)
    String getSendInfo(@RequestParam("serviceName") String serviceName);
}
