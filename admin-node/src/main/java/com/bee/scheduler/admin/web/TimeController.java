package com.bee.scheduler.daemonnode.web;

import com.bee.scheduler.daemonnode.model.HttpResponseBodyWrapper;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class TimeController {
    @Autowired
    private Scheduler scheduler;

    @ResponseBody
    @RequestMapping("/configs")
    HttpResponseBodyWrapper configs() throws Exception {
        HashMap<Object, Object> model = new HashMap<>();
        model.put("clusterMode", scheduler.getMetaData().isJobStoreClustered());
        return new HttpResponseBodyWrapper(model);
    }
}