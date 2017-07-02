package com.bee.lemon.web;

import com.bee.lemon.model.HttpResponseBodyWrapper;
import com.bee.lemon.model.Pageable;
import com.bee.lemon.model.TaskHistory;
import com.bee.lemon.service.TaskService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
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
    public HttpResponseBodyWrapper taskHistoryList(String fireId, String taskName, String taskGroup, String state, Integer triggerType, Long beginTime, Long endTime, Integer page) throws Exception {
        Map<String, Object> model = new HashMap<>();
        // 查询任务历史信息
        Pageable<TaskHistory> result = taskService.queryTaskHistory(scheduler.getSchedulerName(), fireId, taskName, taskGroup, state, triggerType, beginTime, endTime, page);
        return new HttpResponseBodyWrapper(result);
    }

    @ResponseBody
    @GetMapping("/task/history/detail")
    public HttpResponseBodyWrapper taskHistoryDetail(String fireId) throws Exception {
        Map<String, Object> model = new HashMap<>();
        return new HttpResponseBodyWrapper(taskService.getTaskHistory(fireId));
    }
}
