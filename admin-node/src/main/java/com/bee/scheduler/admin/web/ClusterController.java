package com.bee.scheduler.admin.web;

import com.bee.scheduler.admin.model.HttpResponseBodyWrapper;
import com.bee.scheduler.admin.service.SchedulerService;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ConditionalOnProperty(name = "cluster")
public class ClusterController {

    @Autowired
    private Scheduler scheduler;
    @Autowired
    private SchedulerService schedulerService;

    @ResponseBody
    @RequestMapping("/cluster/nodes")
    HttpResponseBodyWrapper nodes() throws Exception {
        return new HttpResponseBodyWrapper(schedulerService.getAllClusterScheduler(scheduler.getSchedulerName()));
    }
}