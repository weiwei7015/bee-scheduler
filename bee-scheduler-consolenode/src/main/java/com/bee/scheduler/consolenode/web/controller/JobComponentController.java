package com.bee.scheduler.consolenode.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.consolenode.model.HttpResponseBodyWrapper;
import com.bee.scheduler.context.task.TaskModuleRegistry;
import com.bee.scheduler.core.ExecutorModule;
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

        for (String taskModuleId : TaskModuleRegistry.TaskModuleMap.keySet()) {
            ExecutorModule taskModule = TaskModuleRegistry.TaskModuleMap.get(taskModuleId);
//            if (taskModule instanceof BuildInJobComponent) {
//                continue;
//            }
            jobComponents.put(taskModuleId, taskModule);
        }

        return new HttpResponseBodyWrapper(jobComponents);
    }
}
