package com.bee.scheduler.consolenode.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping({"/", ""})
    String home() {
        return "forward:/public/main.html";
    }
}