package com.bee.lemon.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
    public void newTask(@RequestBody JSONObject task) throws Exception {
        String name = StringUtils.trim(task.getString("name"));
        String group = StringUtils.trimToNull(task.getString("group"));
        String job = StringUtils.trim(task.getString("jobComponent"));
        Integer scheduleType = task.getInteger("scheduleType");
        String params = StringUtils.trimToEmpty(task.getString("params"));
        String description = StringUtils.trim(task.getString("description"));


        if (StringUtils.isEmpty(job)) {
            throw new BizzException(BizzException.error_code_invalid_params, "请选择Job组件");
        }
        if (StringUtils.isEmpty(name)) {
            throw new BizzException(BizzException.error_code_invalid_params, "请输入任务名称");
        }


        if (StringUtils.isNotEmpty(params)) {
            try {
                JSON.parseObject(params);
            } catch (Exception e) {
                throw new BizzException(BizzException.error_code_invalid_params, "任务参数输入有误，必须是JSON格式");
            }
        }


        Class<? extends JobComponent> jobComponentClass = RamStore.jobs.get(job).getClass();
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(Constants.TASK_PARAM_JOB_DATA_KEY, params);
        JobDetail jobDetail = JobBuilder.newJob(jobComponentClass).withIdentity(name, group).build();

        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(name, group).usingJobData(dataMap).withDescription(description);


        if (scheduleType == 1) {
            JSONObject triggerOptions = task.getJSONObject("scheduleTypeSimpleOptions");
            long interval = triggerOptions.getLongValue("interval");
            int repeatType = triggerOptions.getIntValue("repeatType");
            int repeatCount = triggerOptions.getIntValue("repeatCount");
            Integer misfireHandlingType = task.getInteger("misfireHandlingType");

            int finalRepeatCount = repeatType == 1 ? -1 : repeatCount;

            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            scheduleBuilder.withIntervalInMilliseconds(interval)
                    .withRepeatCount(finalRepeatCount);

            if (misfireHandlingType == 1) {
                scheduleBuilder.withMisfireHandlingInstructionFireNow();
            } else if (misfireHandlingType == 2) {
                scheduleBuilder.withMisfireHandlingInstructionNextWithExistingCount();
            } else if (misfireHandlingType == 3) {
                scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
            } else if (misfireHandlingType == 4) {
                scheduleBuilder.withMisfireHandlingInstructionNextWithRemainingCount();
            } else if (misfireHandlingType == 5) {
                scheduleBuilder.withMisfireHandlingInstructionNowWithExistingCount();
            } else if (misfireHandlingType == 6) {
                scheduleBuilder.withMisfireHandlingInstructionNowWithRemainingCount();
            }


            SimpleTrigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
        } else if (scheduleType == 2) {
            JSONObject triggerOptions = task.getJSONObject("scheduleTypeCalendarIntervalOptions");
            int interval = triggerOptions.getIntValue("interval");
            int intervalUnit = triggerOptions.getIntValue("intervalUnit");
            Integer misfireHandlingType = task.getInteger("misfireHandlingType");

            DateBuilder.IntervalUnit finalIntervalUnit = null;
            switch (intervalUnit) {
                case 1:
                    finalIntervalUnit = DateBuilder.IntervalUnit.SECOND;
                    break;
                case 2:
                    finalIntervalUnit = DateBuilder.IntervalUnit.MINUTE;
                    break;
                case 3:
                    finalIntervalUnit = DateBuilder.IntervalUnit.HOUR;
                    break;
                case 4:
                    finalIntervalUnit = DateBuilder.IntervalUnit.DAY;
                    break;
                case 5:
                    finalIntervalUnit = DateBuilder.IntervalUnit.MONTH;
                    break;
                case 6:
                    finalIntervalUnit = DateBuilder.IntervalUnit.WEEK;
                    break;
                case 7:
                    finalIntervalUnit = DateBuilder.IntervalUnit.YEAR;
                    break;
            }

            CalendarIntervalScheduleBuilder scheduleBuilder = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
            scheduleBuilder.withInterval(interval, finalIntervalUnit);


            if (misfireHandlingType == 1) {
                scheduleBuilder.withMisfireHandlingInstructionDoNothing();
            } else if (misfireHandlingType == 2) {
                scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
            } else if (misfireHandlingType == 3) {
                scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
            }


            CalendarIntervalTrigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
        } else if (scheduleType == 3) {
            JSONObject triggerOptions = task.getJSONObject("scheduleTypeDailyTimeIntervalOptions");
            int interval = triggerOptions.getIntValue("interval");
            int intervalUnit = triggerOptions.getIntValue("intervalUnit");
            Date startTimeOfDay = triggerOptions.getDate("startTimeOfDay");
            Date endTimeOfDay = triggerOptions.getDate("endTimeOfDay");
            JSONArray daysOfWeek = triggerOptions.getJSONArray("daysOfWeek");
            Integer misfireHandlingType = task.getInteger("misfireHandlingType");

            DateBuilder.IntervalUnit finalIntervalUnit = null;
            switch (intervalUnit) {
                case 1:
                    finalIntervalUnit = DateBuilder.IntervalUnit.SECOND;
                    break;
                case 2:
                    finalIntervalUnit = DateBuilder.IntervalUnit.MINUTE;
                    break;
                case 3:
                    finalIntervalUnit = DateBuilder.IntervalUnit.HOUR;
                    break;
            }


            Integer[] finalDaysOfWeek = daysOfWeek.toArray(new Integer[daysOfWeek.size()]);

            DailyTimeIntervalScheduleBuilder scheduleBuilder = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule();
            scheduleBuilder.withInterval(interval, finalIntervalUnit)
                    .startingDailyAt(TimeOfDay.hourAndMinuteAndSecondFromDate(startTimeOfDay))
                    .endingDailyAt(TimeOfDay.hourAndMinuteAndSecondFromDate(endTimeOfDay))
                    .onDaysOfTheWeek(finalDaysOfWeek);

            if (misfireHandlingType == 1) {
                scheduleBuilder.withMisfireHandlingInstructionDoNothing();
            } else if (misfireHandlingType == 2) {
                scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
            } else if (misfireHandlingType == 3) {
                scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
            }

            DailyTimeIntervalTrigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
        } else if (scheduleType == 4) {
            JSONObject triggerOptions = task.getJSONObject("scheduleTypeCronOptions");
            String cron = triggerOptions.getString("cron");
            Integer misfireHandlingType = task.getInteger("misfireHandlingType");


            if (!CronExpression.isValidExpression(cron)) {
                throw new BizzException(BizzException.error_code_invalid_params, "Cron表达式输入有误");
            }

            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);

            if (misfireHandlingType == 1) {
                scheduleBuilder.withMisfireHandlingInstructionDoNothing();
            } else if (misfireHandlingType == 2) {
                scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
            } else if (misfireHandlingType == 3) {
                scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
            }

            CronTrigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();
            scheduler.scheduleJob(jobDetail, trigger);
        }

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
