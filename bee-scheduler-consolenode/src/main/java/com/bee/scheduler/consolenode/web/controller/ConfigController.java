package com.bee.scheduler.consolenode.web.controller;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class ConfigController {
    @Autowired
    private Scheduler scheduler;

    @RequestMapping("/configs")
    public ResponseEntity<HashMap<Object, Object>> configs() throws Exception {
        HashMap<Object, Object> model = new HashMap<>();
        model.put("clusterMode", scheduler.getMetaData().isJobStoreClustered());
        return ResponseEntity.ok(model);
    }
}