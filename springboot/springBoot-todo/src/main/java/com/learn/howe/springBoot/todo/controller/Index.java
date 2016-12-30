package com.learn.howe.springBoot.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

/**
 * @Author Karl
 * @Date 2016/12/28 15:22
 */
@Controller
@RequestMapping("/")
public class Index {

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("list", Arrays.asList("错过的太多，结局太心痛。",
                "也许遗憾和年轻总绑在一起。",
                "往往，心中最爱的那个人，最后却离自己最远。",
                "错过的本来就不是你的。",
                "错过的应该就是要忘记。",
                "遥远的距离，都是因为太过聪明。",
                "其实我心里明白，永远远得很。"));
        return "index";
    }
}
