package com.bee.scheduler.consolenode.service;


import com.bee.scheduler.consolenode.model.ExecutedTask;
import com.bee.scheduler.consolenode.model.Pageable;
import com.bee.scheduler.consolenode.model.TaskDetail;

import java.util.List;

/**
 * @author weiwei
 */
public interface TaskService {
    Pageable<TaskDetail> queryTask(String schedulerName, String keyword, int page);

    List<String> taskQuerySuggestion(String schedulerName, String input);

    ExecutedTask getTaskHistory(String fireId);

    Pageable<ExecutedTask> queryTaskHistory(String schedulerName, String keyword, int page);

    List<String> taskHistoryQuerySuggestion(String schedulerName, String input);
}
