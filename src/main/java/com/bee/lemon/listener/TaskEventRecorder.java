package com.bee.lemon.listener;


import com.bee.lemon.core.RamStore;
import com.bee.lemon.core.SpringApplicationContext;
import com.bee.lemon.core.job.JobExecutionContextHelper;
import com.bee.lemon.model.Notification;
import com.bee.lemon.model.Notification.NotificationType;
import com.bee.lemon.model.TaskHistory;
import com.bee.lemon.model.TaskHistory.TaskExecState;
import com.bee.lemon.service.TaskService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.lf5.LogLevel;
import org.quartz.*;
import org.quartz.Trigger.CompletedExecutionInstruction;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author weiwei 任务事件监听， 存储到通知集合
 */
public class TaskEventRecorder implements JobListener, TriggerListener, SchedulerListener {
    @SuppressWarnings("unused")
    private Log logger = LogFactory.getLog(getClass());

    @Override
    public String getName() {
        return "TaskEventRecorder";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();

        JobExecutionContextHelper.appendExecLog(context, "任务[" + jobDetail.getKey() + "]将开始执行");

        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("taskName", context.getJobDetail().getKey().getName());
        content.put("taskGroup", context.getJobDetail().getKey().getGroup());
        content.put("fireTime", context.getFireTime().getTime());
        content.put("fireId", context.getFireInstanceId());
        RamStore.addNotification(new Notification(Notification.NotificationType.JOB_TO_BEEXECUTED, content));
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        Trigger trigger = context.getTrigger();
        Date currentTime = Calendar.getInstance().getTime();

        JobExecutionContextHelper.appendExecLog(context, "任务[" + jobDetail.getKey() + "]已被取消执行！");

        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("taskName", jobDetail.getKey().getName());
        content.put("taskGroup", jobDetail.getKey().getGroup());
        content.put("fireTime", context.getFireTime().getTime());
        content.put("fireId", context.getFireInstanceId());
        content.put("completeTime", currentTime.getTime());
        content.put("expendTime", context.getJobRunTime());
        content.put("state", "fail");
        RamStore.addNotification(new Notification(Notification.NotificationType.JOB_EXECUTION_VETOED, content));

        // 记录执行历史
        String taskExecLog = JobExecutionContextHelper.getExecLog(context);
        TaskHistory.TaskExecState taskExecState = TaskHistory.TaskExecState.VETOED;
        int refired = 1;
        int triggerType = (trigger instanceof CronTrigger) ? TaskHistory.TRIGGER_TYPE_SCHEDULER : TaskHistory.TRIGGER_TYPE_CONTRIVED;
        TaskHistory taskHistory = new TaskHistory(context.getFireInstanceId(), jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), context.getFireTime(), currentTime, context.getJobRunTime(), refired, taskExecState, triggerType, taskExecLog);
        getTaskService().insertTaskHistory(taskHistory);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        JobDetail jobDetail = context.getJobDetail();
        Trigger trigger = context.getTrigger();
        Date currentTime = Calendar.getInstance().getTime();

        if (jobException == null) {
            JobExecutionContextHelper.appendExecLog(context, "任务[" + jobDetail.getKey() + "]执行成功！");
        } else {
            JobExecutionContextHelper.appendExecLog(context, "任务[" + jobDetail.getKey() + "]失败！");
            JobExecutionContextHelper.appendExecLog(context, "异常详情：");
            JobExecutionContextHelper.appendExecLog(context, "摘要 -> " + jobException.getMessage());
            JobExecutionContextHelper.appendExecLog(context, "明细 -> " + jobException);

        }

        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("taskName", jobDetail.getKey().getName());
        content.put("taskGroup", jobDetail.getKey().getGroup());
        content.put("fireTime", context.getFireTime().getTime());
        content.put("fireId", context.getFireInstanceId());
        content.put("completeTime", currentTime.getTime());
        content.put("expendTime", context.getJobRunTime());
        if (jobException == null) {
            content.put("state", "success");
        } else {
            content.put("state", "fail");
        }
        RamStore.addNotification(new Notification(NotificationType.JOB_WAS_EXECUTED, content));

        // 记录执行历史
        String taskExecLog = JobExecutionContextHelper.getExecLog(context);
        TaskExecState taskExecState = jobException == null ? TaskExecState.SUCCESS : TaskExecState.FAIL;
        int refired = 1;
        int triggerType = (trigger instanceof CronTrigger) ? TaskHistory.TRIGGER_TYPE_SCHEDULER : TaskHistory.TRIGGER_TYPE_CONTRIVED;
        TaskHistory taskHistory = new TaskHistory(context.getFireInstanceId(), jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), context.getFireTime(), currentTime, context.getJobRunTime(), refired, taskExecState, triggerType, taskExecLog);
        getTaskService().insertTaskHistory(taskHistory);
    }

    @Override
    public void jobScheduled(Trigger trigger) {
        // ...
        logger.info("TaskEventRecorder.jobScheduled()");
    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
        // ...

    }

    @Override
    public void triggerFinalized(Trigger trigger) {
        // ...
        logger.info("TaskEventRecorder.triggerFinalized()");
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {
        // ...
        logger.info("TaskEventRecorder.triggerPaused()");
    }

    @Override
    public void triggersPaused(String triggerGroup) {
        // ...
        logger.info("TaskEventRecorder.triggersPaused()");
    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {
        // ...
        logger.info("TaskEventRecorder.triggerResumed()");
    }

    @Override
    public void triggersResumed(String triggerGroup) {
        // ...
        logger.info("TaskEventRecorder.triggersResumed()");
    }

    @Override
    public void jobAdded(JobDetail jobDetail) {
        // ...
        logger.info("TaskEventRecorder.jobAdded()");
    }

    @Override
    public void jobDeleted(JobKey jobKey) {
        // ...
        logger.info("TaskEventRecorder.jobDeleted()");
    }

    @Override
    public void jobPaused(JobKey jobKey) {
        // ...
        logger.info("TaskEventRecorder.jobPaused()");
    }

    @Override
    public void jobsPaused(String jobGroup) {
        // ...
        logger.info("TaskEventRecorder.jobsPaused()");
    }

    @Override
    public void jobResumed(JobKey jobKey) {
        // ...
        logger.info("TaskEventRecorder.jobResumed()");
    }

    @Override
    public void jobsResumed(String jobGroup) {
        // ...

    }

    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        // ...

    }

    @Override
    public void schedulerInStandbyMode() {
        // ...

    }

    @Override
    public void schedulerStarted() {
        // ...

    }

    @Override
    public void schedulerStarting() {
        // ...

    }

    @Override
    public void schedulerShutdown() {
        // ...

    }

    @Override
    public void schedulerShuttingdown() {
        // ...

    }

    @Override
    public void schedulingDataCleared() {
        // ...

    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        // ...
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        // 如果有相同的任务正在运行，否决此次触发
        List<JobExecutionContext> currentlyExecutingJobs;
        try {
            currentlyExecutingJobs = context.getScheduler().getCurrentlyExecutingJobs();
            for (JobExecutionContext jobExecutionContext : currentlyExecutingJobs) {
                JobDetail jobDetail = jobExecutionContext.getJobDetail();
                if (jobDetail.getKey().equals(trigger.getJobKey())) {
                    JobExecutionContextHelper.appendExecLog(context, "任务[" + jobDetail.getKey() + "]正在运行，本次执行将被取消！", LogLevel.WARN);
                    return true;
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        // ...
        logger.info("TaskEventRecorder.triggerMisfired()");
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
        // ...
        logger.info("TaskEventRecorder.triggerComplete()");
    }

    public TaskService getTaskService() {
        return SpringApplicationContext.getBean(TaskService.class);
    }
}