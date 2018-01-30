package com.bee.scheduler.admin.service;


import com.bee.scheduler.admin.model.ExecutedTask;
import com.bee.scheduler.admin.model.ExecutingTask;
import com.bee.scheduler.admin.model.Pageable;
import com.bee.scheduler.admin.model.Task;
import com.bee.scheduler.core.Constants;

import java.util.Date;
import java.util.List;

/**
 * @author weiwei
 */
public interface TaskService {
    Task getTask(String schedulerName, String name, String group);

    Pageable<Task> queryTask(String schedulerName, String keyword, int page);

    int queryTaskCount(String schedulerName, String name, String group, String state);

    List<ExecutingTask> queryExcutingTask(String schedulerName);

    int insertTaskHistory(ExecutedTask taskHistory);

    int insertTaskHistories(List<ExecutedTask> taskHistoryList);

    ExecutedTask getTaskHistory(String fireId);

    Pageable<ExecutedTask> queryTaskHistory(String schedulerName, String fireId, String taskName, String taskGroup, Constants.TaskExecState execState, Constants.TaskFiredWay firedWay, Long starTimeFrom, Long starTimeTo, Integer page, Integer pageSize);

    List<String> getTaskHistoryGroups(String schedulerName);

    int clearHistoryBefore(String schedulerName, Date date);
}
