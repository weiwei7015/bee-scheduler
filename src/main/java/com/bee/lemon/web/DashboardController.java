package com.bee.lemon.web;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author weiwei
 */
@Controller
public class DashboardController {

    @Autowired
    private Scheduler scheduler;

    @RequestMapping("/dashboard")
    public ModelAndView dashboard() throws Exception {
        HashMap<String, Object> model = new HashMap<String, Object>();
        return new ModelAndView("dashboard").addAllObjects(model);
    }

    @ResponseBody
    @RequestMapping("/dashboard/data")
    public Map<String, Object> data() throws Exception {
        HashMap<String, Object> data = new HashMap<String, Object>();
        return data;
    }
}
