package com.bee.scheduler.admin.web;

import com.bee.scheduler.admin.model.*;
import com.bee.scheduler.admin.service.TaskService;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author weiwei
 */
@Controller
public class DashboardController {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TaskService taskService;

    @RequestMapping("/dashboard")
    public ModelAndView dashboard() throws Exception {
        HashMap<String, Object> model = new HashMap<String, Object>();
        return new ModelAndView("dashboard").addAllObjects(model);
    }

    @ResponseBody
    @GetMapping("/dashboard/data")
    public HttpResponseBodyWrapper data() throws Exception {
        HashMap<String, Object> data = new HashMap<>();

        String schedulerName = scheduler.getSchedulerName();
        int taskTotalCount = taskService.queryTaskCount(schedulerName, null, null, null);
        List<ExecutingTask> executingTaskList = taskService.queryExcutingTask(schedulerName);

        Pageable<Task> taskList = taskService.queryTask(schedulerName, null, null, null, 1);
        Pageable<TaskHistory> taskHistoryList = taskService.queryTaskHistory(schedulerName, null, null, null, null, null, null, null, 1);

        data.put("taskTotalCount", taskTotalCount);
        data.put("taskList", taskList);
        data.put("executingTaskList", executingTaskList);
        data.put("taskHistoryList", taskHistoryList);
        return new HttpResponseBodyWrapper(data);
    }

    @ResponseBody
    @GetMapping("/dashboard/executing-task")
    public HttpResponseBodyWrapper executingTask() throws Exception {
        String schedulerName = scheduler.getSchedulerName();
        return new HttpResponseBodyWrapper(taskService.queryExcutingTask(schedulerName));
    }

    @ResponseBody
    @GetMapping("/dashboard/task-trends")
    public HttpResponseBodyWrapper taskTrends() throws Exception {
        String schedulerName = scheduler.getSchedulerName();
        return new HttpResponseBodyWrapper(taskService.queryExcutingTask(schedulerName));
    }
}
