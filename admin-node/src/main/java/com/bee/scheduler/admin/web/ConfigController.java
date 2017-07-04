package com.bee.scheduler.admin.web;

import com.bee.scheduler.admin.model.HttpResponseBodyWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class ConfigController {
    @Autowired
    private Environment env;
    @ResponseBody
    @RequestMapping("/configs")
    HttpResponseBodyWrapper configs() {
        HashMap<Object, Object> model = new HashMap<>();
        model.put("clusterMode", env.containsProperty("clusterMode"));
        return new HttpResponseBodyWrapper(model);
    }
}