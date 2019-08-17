package com.bee.scheduler.consolenode.service;


import com.bee.scheduler.consolenode.model.ExecutedTask;
import com.bee.scheduler.consolenode.model.ExecutingTask;
import com.bee.scheduler.consolenode.model.Pageable;
import com.bee.scheduler.consolenode.model.Task;

import java.util.Date;
import java.util.List;

/**
 * @author weiwei
 */
public interface TaskService {
    Task getTask(String schedulerName, String name, String group);

    Pageable<Task> queryTask(String schedulerName, String keyword, int page);

    List<String> taskQuerySuggestion(String schedulerName, String input);

    int queryTaskCount(String schedulerName, String name, String group, String state);

    List<ExecutingTask> queryExcutingTask(String schedulerName);

    int insertTaskHistory(ExecutedTask taskHistory);

    int insertTaskHistories(List<ExecutedTask> taskHistoryList);

    ExecutedTask getTaskHistory(String fireId);

    Pageable<ExecutedTask> queryTaskHistory(String schedulerName, String keyword, int page);

    List<String> getTaskHistoryGroups(String schedulerName);

    int clearHistoryBefore(String schedulerName, Date date);

    List<String> taskHistoryQuerySuggestion(String schedulerName, String input);
}
