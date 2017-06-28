package com.bee.lemon.service.impl;

import com.bee.lemon.dao.TaskHistoryDao;
import com.bee.lemon.model.Pageable;
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

    @Override
    public int insertTaskHistories(List<TaskHistory> taskHistoryList) {
        return taskHistoryDao.insert(taskHistoryList);
    }

    @Override
    public int insertTaskHistory(TaskHistory taskHistory) {
        List<TaskHistory> histories = new ArrayList<>();
        histories.add(taskHistory);
        return insertTaskHistories(histories);
    }

    @Override
    public Pageable<TaskHistory> queryTaskHistories(String fireId, String taskName, String taskGroup, String state, Integer triggerType, Long beginTime, Long endTime, Integer page) {
        // 默认值处理
        if (page == null) {
            page = 1;
        }
        fireId = StringUtils.trimToNull(fireId);
        taskName = StringUtils.trimToNull(taskName);
        taskGroup = StringUtils.trimToNull(taskGroup);
        state = StringUtils.trimToNull(state);
        return taskHistoryDao.query(fireId, taskName, taskGroup, state, triggerType, beginTime, endTime, page);
    }

    @Override
    public TaskHistory queryHistory(String fireId) {
        return taskHistoryDao.query(fireId);
    }

    @Override
    public List<String> getTaskHistoryGroups() {
        return taskHistoryDao.getTaskHistoryGroups();
    }

    @Override
    public int clearHistoryBefore(Date date) {
        return taskHistoryDao.clearBefore(date);
    }

}
