package com.bee.scheduler.consolenode.web;

import com.bee.scheduler.consolenode.model.HttpResponseBodyWrapper;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class SettingsController {
    @Autowired
    private Scheduler scheduler;

    @ResponseBody
    @GetMapping("/settings/meta")
    HttpResponseBodyWrapper meta() throws Exception {
        HashMap<Object, Object> model = new HashMap<>();
        return new HttpResponseBodyWrapper(scheduler.getMetaData());
    }
}