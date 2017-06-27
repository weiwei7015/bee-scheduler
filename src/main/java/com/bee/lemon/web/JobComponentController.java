package com.bee.lemon.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.lemon.core.RamStore;
import com.bee.lemon.core.job.JobComponent;
import com.bee.lemon.exception.BizzException;
import com.bee.lemon.model.HttpResponseBodyWrapper;
import com.bee.lemon.model.Task;
import com.bee.lemon.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
