package com.bee.lemon.service.impl;

import com.bee.lemon.dao.TaskDao;
import com.bee.lemon.dao.TaskHistoryDao;
import com.bee.lemon.model.Pageable;
import com.bee.lemon.model.Task;
import com.bee.lemon.model.TaskHistory;
import com.bee.lemon.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskHistoryDao taskHistoryDao;
    @Autowired
    private TaskDao taskDao;

    @Override
    public Task getTask(String schedulerName, String name, String group) {
        return taskDao.get(name, group);
    }

    @Override
    public Pageable<Task> queryTask(String schedulerName, String name, String group, String state, int page) {
        return taskDao.query(schedulerName, name, group, state, page);
    }

    @Override
    public TaskHistory getTaskHistory(String fireId) {
        return taskHistoryDao.query(fireId);
    }

    @Override
    public Pageable<TaskHistory> queryTaskHistory(String schedulerName, String fireId, String taskName, String taskGroup, String state, Integer triggerType, Long beginTime, Long endTime, Integer page) {
        // 默认值处理
        if (page == null) {
            page = 1;
        }
        fireId = StringUtils.trimToNull(fireId);
        taskName = StringUtils.trimToNull(taskName);
        taskGroup = StringUtils.trimToNull(taskGroup);
        state = StringUtils.trimToNull(state);
        return taskHistoryDao.query(schedulerName, fireId, taskName, taskGroup, state, triggerType, beginTime, endTime, page);
    }

    @Override
    public List<String> getTaskHistoryGroups(String schedulerName) {
        return taskHistoryDao.getTaskHistoryGroups(schedulerName);
    }

    @Override
    public int insertTaskHistory(TaskHistory taskHistory) {
        List<TaskHistory> histories = new ArrayList<>();
        histories.add(taskHistory);
        return insertTaskHistories(histories);
    }

    @Override
    public int insertTaskHistories(List<TaskHistory> taskHistoryList) {
        return taskHistoryDao.insert(taskHistoryList);
    }

    @Override
    public int clearHistoryBefore(String schedulerName, Date date) {
        return taskHistoryDao.clearBefore(schedulerName, date);
    }
}
