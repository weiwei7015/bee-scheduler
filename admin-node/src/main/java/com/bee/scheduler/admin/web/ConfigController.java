package com.bee.scheduler.admin.web;

import com.bee.scheduler.admin.model.HttpResponseBodyWrapper;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class ConfigController {
    @Autowired
    private Scheduler scheduler;

    @ResponseBody
    @RequestMapping("/server-time")
    HttpResponseBodyWrapper configs() throws Exception {
        return new HttpResponseBodyWrapper(new Date().getTime());
    }

}