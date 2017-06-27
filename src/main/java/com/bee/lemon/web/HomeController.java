package com.bee.lemon.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping({"/", ""})
    String home() {
        System.out.println("HomeController.home");
        return "redirect:/public/index.html";
    }
}