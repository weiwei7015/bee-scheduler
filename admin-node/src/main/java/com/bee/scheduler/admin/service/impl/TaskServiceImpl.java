package com.bee.scheduler.admin.service.impl;

import com.bee.scheduler.admin.dao.TaskDao;
import com.bee.scheduler.admin.dao.TaskHistoryDao;
import com.bee.scheduler.admin.model.ExecutedTask;
import com.bee.scheduler.admin.model.ExecutingTask;
import com.bee.scheduler.admin.model.Pageable;
import com.bee.scheduler.admin.model.Task;
import com.bee.scheduler.admin.service.TaskService;
import com.bee.scheduler.core.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskHistoryDao taskHistoryDao;
    @Autowired
    private TaskDao taskDao;

    @Override
    public Task getTask(String schedulerName, String name, String group) {
        return taskDao.get(schedulerName, name, group);
    }

    @Override
    public Pageable<Task> queryTask(String schedulerName, String keyword, int page) {
        String name = null, group = null, state = null;
        for (String kwItem : StringUtils.split(keyword, " ")) {
            if (Pattern.matches("g:.+", kwItem)) {
                group = StringUtils.split(kwItem, ":")[1];
            } else if (Pattern.matches("s:.+", kwItem)) {
                state = StringUtils.split(kwItem, ":")[1];
            } else {
                name = kwItem;
            }
        }
        return taskDao.query(schedulerName, name, group, state, page);
    }

    @Override
    public int queryTaskCount(String schedulerName, String name, String group, String state) {
        return taskDao.queryCount(schedulerName, name, group, state);
    }

    @Override
    public List<ExecutingTask> queryExcutingTask(String schedulerName) {
        return taskDao.queryExecuting(schedulerName);
    }

    @Override
    public ExecutedTask getTaskHistory(String fireId) {
        return taskHistoryDao.query(fireId);
    }

    @Override
    public Pageable<ExecutedTask> queryTaskHistory(String schedulerName, String fireId, String taskName, String taskGroup, Constants.TaskExecState execState, Constants.TaskFiredWay firedWay, Long starTimeFrom, Long starTimeTo, Integer page, Integer pageSize) {
        // 默认值处理
        if (page == null) {
            page = 1;
        }
        fireId = StringUtils.trimToNull(fireId);
        taskName = StringUtils.trimToNull(taskName);
        taskGroup = StringUtils.trimToNull(taskGroup);
        return taskHistoryDao.query(schedulerName, fireId, taskName, taskGroup, execState, firedWay, starTimeFrom, starTimeTo, page, pageSize);
    }

    @Override
    public List<String> getTaskHistoryGroups(String schedulerName) {
        return taskHistoryDao.getTaskHistoryGroups(schedulerName);
    }

    @Override
    public int insertTaskHistory(ExecutedTask taskHistory) {
        List<ExecutedTask> histories = new ArrayList<>();
        histories.add(taskHistory);
        return insertTaskHistories(histories);
    }

    @Override
    public int insertTaskHistories(List<ExecutedTask> taskHistoryList) {
        return taskHistoryDao.insert(taskHistoryList);
    }

    @Override
    public int clearHistoryBefore(String schedulerName, Date date) {
        return taskHistoryDao.clearBefore(schedulerName, date);
    }
}
