package com.howe.learn.java.springcloud.ribbon.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {

    @GetMapping("/add")
    public String add(Integer a, Integer b, HttpServletRequest request) {
        return "Form port: " + request.getServerPort() + ": Result: " + (a + b);
    }
}
