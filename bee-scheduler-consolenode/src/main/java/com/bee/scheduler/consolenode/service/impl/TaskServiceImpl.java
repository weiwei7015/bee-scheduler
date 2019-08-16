package com.bee.scheduler.consolenode.service.impl;

import com.bee.scheduler.consolenode.dao.TaskDao;
import com.bee.scheduler.consolenode.dao.TaskHistoryDao;
import com.bee.scheduler.consolenode.model.ExecutedTask;
import com.bee.scheduler.consolenode.model.ExecutingTask;
import com.bee.scheduler.consolenode.model.Pageable;
import com.bee.scheduler.consolenode.model.Task;
import com.bee.scheduler.consolenode.service.TaskService;
import com.bee.scheduler.context.common.TaskExecState;
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
    public Pageable<ExecutedTask> queryTaskHistory(String schedulerName, String keyword, int page) {
        String fireId = null, taskName = null, taskGroup = null, execState = null, firedWay = null, instanceId = null;
        Long starTimeFrom = null, starTimeTo = null;
        for (String kwItem : StringUtils.split(keyword, " ")) {
            if (Pattern.matches("id:.+", kwItem)) {
                fireId = StringUtils.split(kwItem, ":")[1];
            } else if (Pattern.matches("g:.+", kwItem)) {
                taskGroup = StringUtils.split(kwItem, ":")[1];
            } else if (Pattern.matches("s:.+", kwItem)) {
                execState = StringUtils.split(kwItem, ":")[1];
            } else if (Pattern.matches("f:.+", kwItem)) {
                firedWay = StringUtils.split(kwItem, ":")[1];
            } else if (Pattern.matches("nd:.+", kwItem)) {
                instanceId = StringUtils.split(kwItem, ":")[1];
//            } else if (Pattern.matches("ts:.+", kwItem)) {
//                starTimeFrom = StringUtils.split(kwItem, ":")[1];
//            } else if (Pattern.matches("te:.+", kwItem)) {
//                starTimeTo = StringUtils.split(kwItem, ":")[1];
            } else {
                taskName = kwItem;
            }
        }


        return taskHistoryDao.query(schedulerName, fireId, taskName, taskGroup, execState, firedWay, instanceId, starTimeFrom, starTimeTo, page);
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

    @Override
    public List<String> getTaskHistoryQuerySuggestions(String input) {
        ArrayList<String> result = new ArrayList<>();
        Pageable<String> taskNames = taskHistoryDao.queryTaskNames(input, 1);
        Pageable<String> taskGroups = taskHistoryDao.queryTaskGroups(input, 1);


        if (StringUtils.isBlank(input)) {
            return result;
        }


        String[] kws = input.split("\\s");


        for (String kw : kws) {
            if (kw.contains(":")) {
                int splitterIndex = kw.indexOf(':');
                String prefix = kw.substring(0, splitterIndex);
                String qs = kw.substring(splitterIndex);

                if (StringUtils.equalsIgnoreCase(prefix, "g")) {
                    result.addAll(taskHistoryDao.queryTaskGroups(qs, 1, 5).getResult());
                } else if (StringUtils.equalsIgnoreCase(prefix, "s")) {
                    for (TaskExecState state : TaskExecState.values()) {
                        if (StringUtils.startsWith(state.name(), qs)) {
                            result.add()
                        }
                    }
                } else if (StringUtils.equalsIgnoreCase(prefix, "f")) {

                }
            } else {
                result.addAll(taskHistoryDao.queryTaskNames(kw, 1, 5).getResult());
            }

        }


        return result;
    }
}
