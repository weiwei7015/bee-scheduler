package com.bee.scheduler.consolenode.web.controller;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class TimeController {
    @Autowired
    private Scheduler scheduler;

    @RequestMapping("/server-time")
    public ResponseEntity<Long> configs() throws Exception {
        return ResponseEntity.ok(new Date().getTime());
    }

}