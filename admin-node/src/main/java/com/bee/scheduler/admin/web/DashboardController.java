package com.bee.scheduler.admin.web;

import com.bee.scheduler.admin.service.TaskService;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

/**
 * @author weiwei
 */
@Controller
public class DashboardController {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TaskService taskService;

    @RequestMapping("/dashboard")
    public ModelAndView dashboard() throws Exception {
        HashMap<String, Object> model = new HashMap<String, Object>();
        return new ModelAndView("dashboard").addAllObjects(model);
    }
}
