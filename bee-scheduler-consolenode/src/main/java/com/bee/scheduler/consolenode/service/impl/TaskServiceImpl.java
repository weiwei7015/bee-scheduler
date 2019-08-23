package com.bee.scheduler.consolenode.service.impl;

import com.bee.scheduler.consolenode.dao.StandardDao;
import com.bee.scheduler.consolenode.model.ExecutedTask;
import com.bee.scheduler.consolenode.model.Pageable;
import com.bee.scheduler.consolenode.model.TaskDetail;
import com.bee.scheduler.consolenode.service.TaskService;
import com.bee.scheduler.context.common.TaskExecState;
import com.bee.scheduler.context.common.TaskFiredWay;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.terracotta.quartz.wrappers.TriggerWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private StandardDao standardDao;

    @Override
    public Pageable<TaskDetail> queryTask(String schedulerName, String keyword, int page) {
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
        return standardDao.queryTask(schedulerName, taskNameList, taskGroupList, taskStateList, page, DEFAULT_PAGE_SIZE);
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
            queryResult.addAll(standardDao.queryTaskGroups(schedulerName, q, 1, 10).getResult());
        } else if (kw.startsWith("s:")) {
            queryPrefix = "s:";
            String q = kw.equals(queryPrefix) ? "" : kw.substring(2);
            for (TriggerWrapper.TriggerState item : TriggerWrapper.TriggerState.values()) {
                if (StringUtils.startsWithIgnoreCase(item.name(), q)) {
                    queryResult.add(item.name());
                }
            }
        } else {
            queryResult.addAll(standardDao.queryTaskNames(schedulerName, kw, 1, 10).getResult());
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
    public ExecutedTask getTaskHistory(String fireId) {
        return standardDao.getTaskHistory(fireId);
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

        return standardDao.queryTaskHistory(schedulerName, fireIdList, taskNameList, taskGroupList, execStateList, firedWayList, instanceIdList, firedTimeBefore, firedTimeAfter, page, DEFAULT_PAGE_SIZE);
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
            queryResult.addAll(standardDao.queryTaskGroups(schedulerName, q, 1, 10).getResult());
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
            queryResult.addAll(standardDao.queryTaskNames(schedulerName, kw, 1, 10).getResult());
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
