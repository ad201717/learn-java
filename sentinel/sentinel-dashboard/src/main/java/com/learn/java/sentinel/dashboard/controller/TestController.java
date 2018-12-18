package com.learn.java.sentinel.dashboard.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {

    @RequestMapping("/test")
    public String test() {
        Entry entry = null;
        try {
            entry = SphU.entry("test");
        } catch (BlockException e) {
            System.out.println("block!");
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
        return "test-" + Math.random();
    }
}
