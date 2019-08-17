package com.bee.scheduler.consolenode.service.impl;

import com.bee.scheduler.consolenode.dao.TaskDao;
import com.bee.scheduler.consolenode.dao.TaskHistoryDao;
import com.bee.scheduler.consolenode.model.ExecutedTask;
import com.bee.scheduler.consolenode.model.ExecutingTask;
import com.bee.scheduler.consolenode.model.Pageable;
import com.bee.scheduler.consolenode.model.Task;
import com.bee.scheduler.consolenode.service.TaskService;
import com.bee.scheduler.context.common.TaskExecState;
import com.bee.scheduler.context.common.TaskFiredWay;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.terracotta.quartz.wrappers.TriggerWrapper;

import java.util.ArrayList;
import java.util.Collections;
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
        List<String> taskNameList = new ArrayList<>();
        List<String> taskGroupList = new ArrayList<>();
        List<String> taskStateList = new ArrayList<>();
        for (String kwItem : StringUtils.split(keyword, " ")) {
            if (Pattern.matches("g:.+", kwItem)) {
                taskGroupList.add(StringUtils.split(kwItem, ":")[1]);
            } else if (Pattern.matches("s:.+", kwItem)) {
                taskStateList.add(StringUtils.split(kwItem, ":")[1]);
            } else {
                taskNameList.add(kwItem);
            }
        }
        return taskDao.query(schedulerName, taskNameList, taskGroupList, taskStateList, page);
    }


    @Override
    public List<String> taskQuerySuggestion(String schedulerName, String input) {
        if (StringUtils.isBlank(input)) {
            return Collections.emptyList();
        }

        int i = input.lastIndexOf(" ");
        if (i == input.length() - 1) {
            return Collections.emptyList();
        }

        String kw = input.substring(i + 1);
        String queryPrefix = "";
        List<String> queryResult = new ArrayList<>();
        if (kw.startsWith("g:")) {
            queryPrefix = "g:";
            String q = kw.equals(queryPrefix) ? "" : kw.substring(2);
            queryResult.addAll(taskDao.queryTaskGroups(schedulerName, q, 1, 10).getResult());
        } else if (kw.startsWith("s:")) {
            queryPrefix = "s:";
            String q = kw.equals(queryPrefix) ? "" : kw.substring(2);
            for (TriggerWrapper.TriggerState item : TriggerWrapper.TriggerState.values()) {
                if (StringUtils.startsWithIgnoreCase(item.name(), q)) {
                    queryResult.add(item.name());
                }
            }
        } else {
            queryResult.addAll(taskDao.queryTaskNames(schedulerName, kw, 1, 10).getResult());
        }
        List<String> suggestions = new ArrayList<>();
        if (i == -1) {
            for (String item : queryResult) {
                suggestions.add(queryPrefix + item + " ");
            }
        } else {
            for (String item : queryResult) {
                suggestions.add(input.substring(0, i + 1) + queryPrefix + item + " ");
            }
        }
        return suggestions;
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
        List<String> fireIdList = new ArrayList<>();
        List<String> taskNameList = new ArrayList<>();
        List<String> taskGroupList = new ArrayList<>();
        List<String> execStateList = new ArrayList<>();
        List<String> firedWayList = new ArrayList<>();
        List<String> instanceIdList = new ArrayList<>();

        Long firedTimeBefore = null, firedTimeAfter = null;
        for (String kwItem : StringUtils.split(keyword, " ")) {
            if (Pattern.matches("id:.+", kwItem)) {
                fireIdList.add(StringUtils.split(kwItem, ":")[1]);
            } else if (Pattern.matches("g:.+", kwItem)) {
                taskGroupList.add(StringUtils.split(kwItem, ":")[1]);
            } else if (Pattern.matches("s:.+", kwItem)) {
                execStateList.add(StringUtils.split(kwItem, ":")[1]);
            } else if (Pattern.matches("f:.+", kwItem)) {
                firedWayList.add(StringUtils.split(kwItem, ":")[1]);
            } else if (Pattern.matches("nd:.+", kwItem)) {
                instanceIdList.add(StringUtils.split(kwItem, ":")[1]);
//            } else if (Pattern.matches("ts:.+", kwItem)) {
//                firedTimeBefore = StringUtils.split(kwItem, ":")[1];
//            } else if (Pattern.matches("te:.+", kwItem)) {
//                firedTimeAfter = StringUtils.split(kwItem, ":")[1];
            } else {
                taskNameList.add(kwItem);
            }
        }

        return taskHistoryDao.query(schedulerName, fireIdList, taskNameList, taskGroupList, execStateList, firedWayList, instanceIdList, firedTimeBefore, firedTimeAfter, page);
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
    public List<String> taskHistoryQuerySuggestion(String schedulerName, String input) {
        if (StringUtils.isBlank(input)) {
            return Collections.emptyList();
        }

        int i = input.lastIndexOf(" ");
        if (i == input.length() - 1) {
            return Collections.emptyList();
        }

        String kw = input.substring(i + 1);
        String queryPrefix = "";
        List<String> queryResult = new ArrayList<>();
        if (kw.startsWith("g:")) {
            queryPrefix = "g:";
            String q = kw.equals(queryPrefix) ? "" : kw.substring(2);
            queryResult.addAll(taskHistoryDao.queryTaskGroups(schedulerName, q, 1, 10).getResult());
        } else if (kw.startsWith("f:")) {
            queryPrefix = "f:";
            String q = kw.equals(queryPrefix) ? "" : kw.substring(2);
            for (TaskFiredWay item : TaskFiredWay.values()) {
                if (StringUtils.startsWithIgnoreCase(item.name(), q)) {
                    queryResult.add(item.name());
                }
            }
        } else if (kw.startsWith("s:")) {
            queryPrefix = "s:";
            String q = kw.equals(queryPrefix) ? "" : kw.substring(2);
            for (TaskExecState item : TaskExecState.values()) {
                if (StringUtils.startsWithIgnoreCase(item.name(), q)) {
                    queryResult.add(item.name());
                }
            }
        } else {
            queryResult.addAll(taskHistoryDao.queryTaskNames(schedulerName, kw, 1, 10).getResult());
        }
        List<String> suggestions = new ArrayList<>();
        if (i == -1) {
            for (String item : queryResult) {
                suggestions.add(queryPrefix + item + " ");
            }
        } else {
            for (String item : queryResult) {
                suggestions.add(input.substring(0, i + 1) + queryPrefix + item + " ");
            }
        }
        return suggestions;
    }
}
