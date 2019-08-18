package com.bee.scheduler.consolenode.web.controller;

import com.bee.scheduler.consolenode.model.ExecutedTask;
import com.bee.scheduler.consolenode.model.Pageable;
import com.bee.scheduler.consolenode.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<List<String>> taskHistoryGroups() throws Exception {
        return ResponseEntity.ok(taskService.getTaskHistoryGroups(scheduler.getSchedulerName()));
    }

    @GetMapping("/task/history/list")
    public ResponseEntity<Pageable<ExecutedTask>> taskHistoryList(String keyword, Integer page) throws Exception {

        keyword = StringUtils.trimToEmpty(keyword);
        page = page == null ? 1 : page;

        // 查询任务历史信息
        Pageable<ExecutedTask> result = taskService.queryTaskHistory(scheduler.getSchedulerName(), keyword, page);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/task/history/detail")
    public ResponseEntity<ExecutedTask> taskHistoryDetail(String fireId) throws Exception {
        ExecutedTask result = taskService.getTaskHistory(fireId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/task/history/query-suggestions")
    public ResponseEntity<List<String>> queryTaskHistoryGroups(String input) throws Exception {
        ArrayList<String> strings = new ArrayList<>();
        return ResponseEntity.ok(taskService.taskHistoryQuerySuggestion(scheduler.getSchedulerName(), input));
    }
}
