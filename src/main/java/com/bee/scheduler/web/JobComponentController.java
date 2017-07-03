package com.bee.scheduler.web;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.RamStore;
import com.bee.scheduler.model.HttpResponseBodyWrapper;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author weiwei
 */
@Controller
public class JobComponentController {
    @Autowired
    private Scheduler scheduler;

    @ResponseBody
    @GetMapping("/job-component/list")
    public HttpResponseBodyWrapper taskHistoryGroups() throws Exception {
        Map<String, Object> model = new HashMap<>();

        JSONObject jobComponents = new JSONObject();

        for (String jobComponentId : RamStore.jobs.keySet()) {
            jobComponents.put(jobComponentId, RamStore.jobs.get(jobComponentId));
        }

        return new HttpResponseBodyWrapper(jobComponents);
    }
}
