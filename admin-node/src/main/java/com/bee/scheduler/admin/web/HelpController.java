package com.bee.scheduler.admin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author weiwei
 */
@Controller
public class HelpController {

    @RequestMapping("/help/job")
    public ModelAndView job(HttpServletRequest request) {
        HashMap<String, Object> model = new HashMap<String, Object>();
        if (request.getParameter("nolayout") != null) {
            model.put("nolayout", true);
        }
        return new ModelAndView("help-job").addAllObjects(model);
    }

    @RequestMapping("/help/sys")
    public ModelAndView sys() {
        HashMap<String, Object> model = new HashMap<String, Object>();
        return new ModelAndView("help-sys").addAllObjects(model);
    }

    @RequestMapping("/help/cron")
    public ModelAndView cron() {
        HashMap<String, Object> model = new HashMap<String, Object>();
        return new ModelAndView("help-cron").addAllObjects(model);
    }
}
