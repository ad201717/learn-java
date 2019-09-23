package com.howe.learn.java.springcloud.hystrix.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@RestController
public class ProviderController {

    @GetMapping("/getDashboard")
    public List<String> getDashboard() throws Exception {
        Random random = new Random();
        int i = random.nextInt(100);
        if (i < 50) {
            return Collections.singletonList("OK");
        } else if (i < 75) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Collections.singletonList("timeout");
        } else {
            throw new Exception("exp");
        }
    }
}
