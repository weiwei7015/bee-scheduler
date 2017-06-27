package com.bee.lemon.service;


import com.bee.lemon.model.Pageable;
import com.bee.lemon.model.TaskHistory;

import java.util.Date;
import java.util.List;

/**
 * @author weiwei1
 */
public interface TaskService {
    public int insertTaskHistories(List<TaskHistory> taskHistoryList);

    public int insertTaskHistory(TaskHistory taskHistory);

    public Pageable<TaskHistory> queryTaskHistories(String fireId, String taskName, String taskGroup, String state, Long beginTime, Long endTime, Integer page);

    public TaskHistory queryHistory(String fireId);

    public List<String> getTaskHistoryGroups();

    public int clearHistoryBefore(Date date);
}
