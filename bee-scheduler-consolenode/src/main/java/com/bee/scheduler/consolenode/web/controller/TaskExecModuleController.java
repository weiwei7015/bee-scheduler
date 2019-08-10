package com.bee.scheduler.consolenode.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.consolenode.model.HttpResponseBodyWrapper;
import com.bee.scheduler.context.task.TaskModuleRegistry;
import com.bee.scheduler.core.ExecutorModule;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author weiwei
 */
@Controller
public class TaskExecModuleController {
    @ResponseBody
    @GetMapping("/task-module/list")
    public HttpResponseBodyWrapper taskModuleList() throws Exception {
        JSONObject taskModules = new JSONObject();
        for (String taskModuleId : TaskModuleRegistry.TaskModuleMap.keySet()) {
            ExecutorModule taskModule = TaskModuleRegistry.TaskModuleMap.get(taskModuleId);
            taskModules.put(taskModuleId, taskModule);
        }
        return new HttpResponseBodyWrapper(taskModules);
    }
}
