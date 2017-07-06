package com.bee.scheduler.admin.web;

import com.bee.scheduler.admin.model.ExecutedTask;
import com.bee.scheduler.admin.model.HttpResponseBodyWrapper;
import com.bee.scheduler.admin.model.Pageable;
import com.bee.scheduler.admin.service.TaskService;
import com.bee.scheduler.core.Constants;
import org.apache.commons.lang3.StringUtils;
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
public class TaskHistoryController {
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private TaskService taskService;


    @ResponseBody
    @GetMapping("/task/history/groups")
    public HttpResponseBodyWrapper taskHistoryGroups() throws Exception {
        Map<String, Object> model = new HashMap<>();
        return new HttpResponseBodyWrapper(taskService.getTaskHistoryGroups(scheduler.getSchedulerName()));
    }

    @ResponseBody
    @GetMapping("/task/history/list")
    public HttpResponseBodyWrapper taskHistoryList(String fireId, String taskName, String taskGroup, String execState, String firedWay, Long beginTime, Long endTime, Integer page) throws Exception {
        Map<String, Object> model = new HashMap<>();


        Constants.TaskExecState enumExecState = StringUtils.isBlank(execState) ? null : Constants.TaskExecState.valueOf(execState);
        Constants.TaskFiredWay enumExecType = StringUtils.isBlank(firedWay) ? null : Constants.TaskFiredWay.valueOf(firedWay);

        // 查询任务历史信息
        Pageable<ExecutedTask> result = taskService.queryTaskHistory(scheduler.getSchedulerName(), fireId, taskName, taskGroup, enumExecState, enumExecType, beginTime, endTime, page, null);
        return new HttpResponseBodyWrapper(result);
    }

    @ResponseBody
    @GetMapping("/task/history/detail")
    public HttpResponseBodyWrapper taskHistoryDetail(String fireId) throws Exception {
        Map<String, Object> model = new HashMap<>();
        return new HttpResponseBodyWrapper(taskService.getTaskHistory(fireId));
    }
}
