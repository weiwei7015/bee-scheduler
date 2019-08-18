package com.bee.scheduler.consolenode.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.context.task.TaskModuleRegistry;
import com.bee.scheduler.core.ExecutorModule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weiwei
 */
@RestController
public class TaskExecModuleController {
    @GetMapping("/task-module/list")
    public ResponseEntity<JSONObject> taskModuleList() throws Exception {
        JSONObject taskModules = new JSONObject();
        for (String taskModuleId : TaskModuleRegistry.TaskModuleMap.keySet()) {
            ExecutorModule taskModule = TaskModuleRegistry.TaskModuleMap.get(taskModuleId);
            taskModules.put(taskModuleId, taskModule);
        }
        return ResponseEntity.ok(taskModules);
    }
}
