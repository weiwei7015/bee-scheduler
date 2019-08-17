package com.bee.scheduler.consolenode.web.controller;

import com.bee.scheduler.consolenode.model.ExecutedTask;
import com.bee.scheduler.consolenode.model.HttpResponseBodyWrapper;
import com.bee.scheduler.consolenode.model.Pageable;
import com.bee.scheduler.consolenode.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author weiwei
 */
@RestController
public class TaskHistoryController {
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private TaskService taskService;

    @GetMapping("/task/history/groups")
    public HttpResponseBodyWrapper taskHistoryGroups() throws Exception {
        Map<String, Object> model = new HashMap<>();
        return new HttpResponseBodyWrapper(taskService.getTaskHistoryGroups(scheduler.getSchedulerName()));
    }

    @GetMapping("/task/history/list")
    public HttpResponseBodyWrapper taskHistoryList(String keyword, Integer page) throws Exception {

        keyword = StringUtils.trimToEmpty(keyword);
        page = page == null ? 1 : page;

        // 查询任务历史信息
        Pageable<ExecutedTask> result = taskService.queryTaskHistory(scheduler.getSchedulerName(), keyword, page);
        return new HttpResponseBodyWrapper(result);
    }

    @GetMapping("/task/history/detail")
    public HttpResponseBodyWrapper taskHistoryDetail(String fireId) throws Exception {
        Map<String, Object> model = new HashMap<>();
        return new HttpResponseBodyWrapper(taskService.getTaskHistory(fireId));
    }

    @GetMapping("/task/history/query-suggestions")
    public HttpResponseBodyWrapper queryTaskHistoryGroups(String input) throws Exception {
        ArrayList<String> strings = new ArrayList<>();
        return new HttpResponseBodyWrapper(taskService.taskHistoryQuerySuggestion(scheduler.getSchedulerName(), input));
    }
}
