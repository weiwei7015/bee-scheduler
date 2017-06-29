package com.bee.lemon.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bee.lemon.core.RamStore;
import com.bee.lemon.core.job.JobComponent;
import com.bee.lemon.exception.BizzException;
import com.bee.lemon.model.HttpResponseBodyWrapper;
import com.bee.lemon.model.Pageable;
import com.bee.lemon.model.Task;
import com.bee.lemon.service.TaskService;
import com.bee.lemon.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * @author weiwei
 */
@Controller
public class TaskController {
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TaskService taskService;

    @ResponseBody
    @GetMapping("/task/list")
    public HttpResponseBodyWrapper task(String state, String taskName, String taskGroup, Integer page) throws Exception {
        state = StringUtils.trimToNull(state);
        taskName = StringUtils.trimToNull(taskName);
        taskGroup = StringUtils.trimToNull(taskGroup);
        page = page == null ? 1 : page;

//        List<Task> taskList = new ArrayList<>();
//        Set<TriggerKey> triggerKeys;
//        if (StringUtils.isEmpty(taskGroup)) {
//            triggerKeys = scheduler.getTriggerKeys(GroupMatcher.<TriggerKey>anyGroup());
//        } else {
//            triggerKeys = scheduler.getTriggerKeys(GroupMatcher.<TriggerKey>groupEquals(taskGroup));
//        }
//        for (TriggerKey triggerKey : triggerKeys) {
//            Trigger trigger = scheduler.getTrigger(triggerKey);
//            JobDetail jobDetail = scheduler.getJobDetail(trigger.getJobKey());
//            if (JobComponent.class.isAssignableFrom(jobDetail.getJobClass())) {
//                Task task = new Task(trigger, jobDetail, scheduler.getTriggerState(triggerKey), RamStore.jobs.get(jobDetail.getJobClass().getName()));
//                if (StringUtils.containsIgnoreCase(jobDetail.getKey().getName(), taskName) && (StringUtils.equals(state, "ALL") || StringUtils.equals(task.getTriggerState().toString(), state))) {
//                    taskList.add(task);
//                }
//            }
//        }

        Pageable<Task> queryResult = taskService.queryTask(taskName, taskGroup, state, page);
        return new HttpResponseBodyWrapper(queryResult);
    }


    @ResponseBody
    @GetMapping("/task/groups")
    public HttpResponseBodyWrapper taskHistoryGroups() throws Exception {
        Map<String, Object> model = new HashMap<>();
        return new HttpResponseBodyWrapper(scheduler.getTriggerGroupNames());
    }


    @ResponseBody
    @PostMapping("/task/new")
    public void newTask(String name, String group, String job, String cron, String params, String description) throws Exception {
        name = StringUtils.trim(name);
        group = StringUtils.trimToNull(group);
        job = StringUtils.trim(job);
        cron = StringUtils.trim(cron);
        params = StringUtils.trimToEmpty(params);
        description = StringUtils.trim(description);
        if (StringUtils.isEmpty(job)) {
            throw new BizzException(BizzException.error_code_invalid_params, "请选择Job组件");
        }
        if (StringUtils.isEmpty(name)) {
            throw new BizzException(BizzException.error_code_invalid_params, "请输入任务名称");
        }
        if (!CronExpression.isValidExpression(cron)) {
            throw new BizzException(BizzException.error_code_invalid_params, "Cron表达式输入有误");
        }
        if (StringUtils.isNotEmpty(params)) {
            try {
                JSON.parseObject(params);
            } catch (Exception e) {
                throw new BizzException(BizzException.error_code_invalid_params, "任务参数输入有误，必须是JSON格式");
            }
        }


        Class<? extends JobComponent> jobClass = RamStore.jobs.get(job).getClass();
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(Constants.TASK_PARAM_JOB_DATA_KEY, params);
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(name, group).build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group).usingJobData(dataMap).withDescription(description).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    @ResponseBody
    @PostMapping("/task/delete")
    public void delete(String name, String group) throws Exception {
        scheduler.unscheduleJob(new TriggerKey(name, group));
    }

    @ResponseBody
    @PostMapping("/task/pause")
    public void pause(String name, String group) throws Exception {
        scheduler.pauseTrigger(new TriggerKey(name, group));
    }

    @ResponseBody
    @PostMapping("/task/resume")
    public void resume(String name, String group) throws Exception {
        scheduler.resumeTrigger(new TriggerKey(name, group));
    }

    @ResponseBody
    @PostMapping("/task/execute")
    public void execute(String name, String group) throws Exception {
        JobKey jobKey = new JobKey(name, group);
        TriggerKey triggerKey = new TriggerKey(name, group);
        JobDataMap jobDataMap = scheduler.getTrigger(triggerKey).getJobDataMap();
        scheduler.triggerJob(jobKey, jobDataMap);
    }

    @ResponseBody
    @GetMapping("/task/detail")
    public HttpResponseBodyWrapper detail(String name, String group) throws Exception {
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(new TriggerKey(name, group));
        JobDetail jobDetail = scheduler.getJobDetail(trigger.getJobKey());
        TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
        Map<String, Object> taskModel = new HashMap<>();
        taskModel.put("jobName", RamStore.jobs.get(jobDetail.getJobClass().getName()).getName());
        taskModel.put("taskName", jobDetail.getKey().getName());
        taskModel.put("taskGroup", jobDetail.getKey().getGroup());
        taskModel.put("cron", trigger.getCronExpression());
        taskModel.put("description", trigger.getDescription());
        taskModel.put("triggerState", triggerState);
        taskModel.put("params", trigger.getJobDataMap().getString(Constants.TASK_PARAM_JOB_DATA_KEY));
        return new HttpResponseBodyWrapper(taskModel);
    }

    @ResponseBody
    @PostMapping("/task/edit")
    public void edit(String name, String group, String cron, String params, String description) throws Exception {
        cron = StringUtils.trimToEmpty(cron);
        params = StringUtils.trimToEmpty(params);
        if (!CronExpression.isValidExpression(cron)) {
            throw new BizzException(BizzException.error_code_invalid_params, "Cron表达式输入有误");
        }
        if (StringUtils.isNotEmpty(params)) {
            try {
                JSON.parseObject(params);
            } catch (Exception e) {
                throw new BizzException(BizzException.error_code_invalid_params, "任务参数输入有误，必须是JSON格式");
            }
        }
        TriggerKey triggerKey = new TriggerKey(name, group);
        Trigger oldTrigger = scheduler.getTrigger(triggerKey);
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(Constants.TASK_PARAM_JOB_DATA_KEY, params);
        CronTrigger newTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(description).withSchedule(CronScheduleBuilder.cronSchedule(cron)).usingJobData(dataMap).forJob(oldTrigger.getJobKey()).build();
        scheduler.rescheduleJob(triggerKey, newTrigger);
    }
}
