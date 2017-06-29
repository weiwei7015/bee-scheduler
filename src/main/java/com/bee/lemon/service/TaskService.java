package com.bee.lemon.service;


import com.bee.lemon.model.Pageable;
import com.bee.lemon.model.Task;
import com.bee.lemon.model.TaskHistory;

import java.util.Date;
import java.util.List;

/**
 * @author weiwei
 */
public interface TaskService {
    int insertTaskHistories(List<TaskHistory> taskHistoryList);

    int insertTaskHistory(TaskHistory taskHistory);

    Pageable<Task> queryTask(String name, String group, String state, int page);

    Pageable<TaskHistory> queryTaskHistory(String fireId, String taskName, String taskGroup, String state, Integer triggerType, Long beginTime, Long endTime, Integer page);

    Task getTask(String name, String group);

    TaskHistory getTaskHistory(String fireId);

    List<String> getTaskHistoryGroups();

    int clearHistoryBefore(Date date);
}
