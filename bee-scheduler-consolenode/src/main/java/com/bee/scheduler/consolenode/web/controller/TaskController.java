package com.bee.scheduler.consolenode.web.controller;

import com.alibaba.fastjson.JSON;
import com.bee.scheduler.consolenode.exception.BizzException;
import com.bee.scheduler.consolenode.model.*;
import com.bee.scheduler.consolenode.service.TaskService;
import com.bee.scheduler.context.task.TaskScheduler;
import com.bee.scheduler.context.common.TaskSpecialGroup;
import com.bee.scheduler.context.model.QuickTaskConfig;
import com.bee.scheduler.context.model.TaskConfig;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author weiwei
 */
@Controller
public class TaskController {
    @Autowired
    private TaskScheduler scheduler;

    @Autowired
    private TaskService taskService;

    @ResponseBody
    @GetMapping("/task/groups")
    public HttpResponseBodyWrapper taskGroups() throws Exception {
        return new HttpResponseBodyWrapper(scheduler.getTaskGroups());
    }

    @ResponseBody
    @GetMapping("/task/list")
    public HttpResponseBodyWrapper task(String keyword, Integer page) throws Exception {
        keyword = StringUtils.trimToEmpty(keyword);
        page = page == null ? 1 : page;

        Pageable<Task> queryResult = taskService.queryTask(scheduler.getSchedulerName(), keyword, page);
        return new HttpResponseBodyWrapper(queryResult);
    }

    @ResponseBody
    @GetMapping("/task/trends")
    public HttpResponseBodyWrapper trends() throws Exception {
        HashMap<String, Object> data = new HashMap<>();

        String schedulerName = scheduler.getSchedulerName();
        int taskTotalCount = taskService.queryTaskCount(schedulerName, null, null, null);
        List<ExecutingTask> executingTaskList = taskService.queryExcutingTask(schedulerName);

        Pageable<ExecutedTask> taskHistoryList = taskService.queryTaskHistory(schedulerName, "", 1);

        List<FiredTask> taskTrends = new ArrayList<>();

        taskTrends.addAll(executingTaskList);
        taskTrends.addAll(taskHistoryList.getResult());

        taskTrends.sort((o1, o2) -> o2.getFiredTime().compareTo(o1.getFiredTime()));

        data.put("taskTotalCount", taskTotalCount);
        data.put("executingTaskCount", executingTaskList.size());
        data.put("taskTrends", taskTrends);
        return new HttpResponseBodyWrapper(data);
    }

    @ResponseBody
    @PostMapping("/task/new")
    public void newTask(@RequestBody TaskConfig taskConfig) throws Exception {
        taskConfig.setName(StringUtils.trimToEmpty(taskConfig.getName()));
        taskConfig.setGroup(StringUtils.trimToEmpty(taskConfig.getGroup()));

        if (StringUtils.isEmpty(taskConfig.getName())) {
            throw new BizzException(BizzException.error_code_invalid_params, "请输入任务名称");
        } else {
            if (!Pattern.matches("^[A-Za-z0-9_]+$", taskConfig.getName())) {
                throw new BizzException(BizzException.error_code_invalid_params, "任务名称只允许使用字母、数字和下划线，请检查");
            }
        }
        if (StringUtils.isEmpty(taskConfig.getGroup())) {
            throw new BizzException(BizzException.error_code_invalid_params, "请输入任务所属组");
        } else {
            if (!Pattern.matches("^[A-Za-z0-9_]+$", taskConfig.getGroup())) {
                throw new BizzException(BizzException.error_code_invalid_params, "任务所属组只允许使用字母、数字和下划线，请检查");
            }
        }
        if (StringUtils.isEmpty(taskConfig.getTaskModule())) {
            throw new BizzException(BizzException.error_code_invalid_params, "请选择任务组件");
        }
        if (StringUtils.isNotEmpty(taskConfig.getParams())) {
            try {
                JSON.parseObject(taskConfig.getParams());
            } catch (Exception e) {
                throw new BizzException(BizzException.error_code_invalid_params, "任务参数输入有误，必须是JSON格式");
            }
        }
        if (StringUtils.isNotEmpty(taskConfig.getLinkageRule())) {
            try {
                JSON.parseArray(taskConfig.getLinkageRule());
            } catch (Exception e) {
                throw new BizzException(BizzException.error_code_invalid_params, "联动任务规则输入有误，必须是JSON格式");
            }
        }
        if (TaskSpecialGroup.contains(taskConfig.getGroup())) {
            throw new BizzException(BizzException.error_code_invalid_params, "任务组不允许使用系统保留关键词:" + taskConfig.getGroup());
        }

        if (taskConfig.getScheduleType() == TaskConfig.SCHEDULE_TYPE_CRON_TRIGGER) {
            TaskConfig.ScheduleTypeCronOptions scheduleOptions = taskConfig.getScheduleTypeCronOptions();
            if (!CronExpression.isValidExpression(scheduleOptions.getCron())) {
                throw new BizzException(BizzException.error_code_invalid_params, "Cron表达式输入有误");
            }
        }
        scheduler.schedule(taskConfig);
    }

    @ResponseBody
    @GetMapping("/task/detail")
    public HttpResponseBodyWrapper detail(String group, String name) throws Exception {
        return new HttpResponseBodyWrapper(scheduler.getTaskConfig(group, name));
    }

    @ResponseBody
    @PostMapping("/task/edit")
    public void edit(@RequestBody TaskConfig taskConfig) throws Exception {
        if (StringUtils.isNotEmpty(taskConfig.getParams())) {
            try {
                JSON.parseObject(taskConfig.getParams());
            } catch (Exception e) {
                throw new BizzException(BizzException.error_code_invalid_params, "任务参数输入有误，必须是JSON格式");
            }
        }
        if (StringUtils.isNotEmpty(taskConfig.getLinkageRule())) {
            try {
                JSON.parseArray(taskConfig.getLinkageRule());
            } catch (Exception e) {
                throw new BizzException(BizzException.error_code_invalid_params, "联动任务规则输入有误，必须是JSON格式");
            }
        }
        if (taskConfig.getScheduleType() == TaskConfig.SCHEDULE_TYPE_CRON_TRIGGER) {
            TaskConfig.ScheduleTypeCronOptions scheduleOptions = taskConfig.getScheduleTypeCronOptions();
            if (!CronExpression.isValidExpression(scheduleOptions.getCron())) {
                throw new BizzException(BizzException.error_code_invalid_params, "Cron表达式输入有误");
            }
        }
        scheduler.reschedule(taskConfig);
    }

    @ResponseBody
    @PostMapping("/task/delete")
    public void delete(String[] taskIds) throws Exception {
        for (String taskId : taskIds) {
            String[] group$name = StringUtils.split(taskId, "-");
            scheduler.unschedule(group$name[0], group$name[1]);
        }
    }

    @ResponseBody
    @PostMapping("/task/pause")
    public void pause(String[] taskIds) throws Exception {
        for (String taskId : taskIds) {
            String[] group$name = StringUtils.split(taskId, "-");
            String group = group$name[0], name = group$name[1];
            scheduler.pause(group, name);
        }
    }

    @ResponseBody
    @PostMapping("/task/resume")
    public void resume(String[] taskIds) throws Exception {
        for (String taskId : taskIds) {
            String[] group$name = StringUtils.split(taskId, "-");
            String group = group$name[0], name = group$name[1];
            scheduler.resume(group, name);
        }
    }

    @ResponseBody
    @PostMapping("/task/execute")
    public void execute(String[] taskIds) throws Exception {
        for (String taskId : taskIds) {
            String[] group$name = StringUtils.split(taskId, "-");
            String name = group$name[1];
            String group = group$name[0];
            scheduler.trigger(group, name);
        }
    }

    @ResponseBody
    @PostMapping("/task/tmp")
    public void quickTask(@RequestBody QuickTaskConfig quickTaskConfig) throws Exception {
        quickTaskConfig.setName(StringUtils.trimToEmpty(quickTaskConfig.getName()));

        if (StringUtils.isEmpty(quickTaskConfig.getName())) {
            throw new BizzException(BizzException.error_code_invalid_params, "请输入任务名称");
        } else {
            if (!Pattern.matches("^[A-Za-z0-9_]+$", quickTaskConfig.getName())) {
                throw new BizzException(BizzException.error_code_invalid_params, "任务名称只允许使用字母、数字和下划线，请检查");
            }
        }

        if (StringUtils.isEmpty(quickTaskConfig.getTaskModule())) {
            throw new BizzException(BizzException.error_code_invalid_params, "请选择任务组件");
        }
        if (StringUtils.isNotEmpty(quickTaskConfig.getParams())) {
            try {
                JSON.parseObject(quickTaskConfig.getParams());
            } catch (Exception e) {
                throw new BizzException(BizzException.error_code_invalid_params, "任务参数输入有误，必须是JSON格式");
            }
        }

        scheduler.quickTask(quickTaskConfig);
    }
}
