package com.learn.howe.springBoot.simpleStub.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Karl
 * @Date 2016/12/28 15:22
 */
@RestController
@RequestMapping("/")
public class Index {

    @RequestMapping("/")
    public String index(){
        return "Welcome!";
    }
}
