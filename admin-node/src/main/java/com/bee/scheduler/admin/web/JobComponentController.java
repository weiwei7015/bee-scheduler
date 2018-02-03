package com.bee.scheduler.admin.web;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.admin.core.RamLocal;
import com.bee.scheduler.admin.model.HttpResponseBodyWrapper;
import com.bee.scheduler.core.job.BuildInJobComponent;
import com.bee.scheduler.core.job.JobComponent;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

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

        for (String jobComponentId : RamLocal.JobComponentMap.keySet()) {
            JobComponent jobComponent = RamLocal.JobComponentMap.get(jobComponentId);
            if (jobComponent instanceof BuildInJobComponent) {
                continue;
            }
            jobComponents.put(jobComponentId, jobComponent);
        }

        return new HttpResponseBodyWrapper(jobComponents);
    }
}
