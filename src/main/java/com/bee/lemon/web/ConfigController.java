package com.bee.lemon.web;

import com.bee.lemon.model.HttpResponseBodyWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class ConfigController {
    @ResponseBody
    @RequestMapping("/configs")
    HttpResponseBodyWrapper configs() {
        return new HttpResponseBodyWrapper(new HashMap<>());
    }
}