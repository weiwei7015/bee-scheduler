package com.bee.scheduler.core.listener;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.JobExecutionContextUtil;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLog;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author weiwei
 */
public abstract class TaskListenerSupport implements JobListener, TriggerListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        taskToBeExecuted(buildTaskExecutionContext(context));
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        taskExecutionVetoed(buildTaskExecutionContext(context));
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        taskWasExecuted(buildTaskExecutionContext(context), jobException);
    }


    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        taskTriggerFired(buildTaskExecutionContext(context));
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return vetoTaskExecution(buildTaskExecutionContext(context));
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        taskTriggerMisfired(trigger);
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        taskTriggerComplete(buildTaskExecutionContext(context), triggerInstructionCode);
    }


    //构建TaskExecutionContext
    private TaskExecutionContext buildTaskExecutionContext(JobExecutionContext context) {
        JSONObject taskParam = JobExecutionContextUtil.getTaskParam(context);
        TaskExecutionLog taskLogger = new TaskExecutionLog(context);

        TaskExecutionContext taskExecutionContext = new TaskExecutionContext();
        taskExecutionContext.setJobExecutionContext(context);
        taskExecutionContext.setTaskParam(taskParam);
        taskExecutionContext.setLogger(taskLogger);
        return taskExecutionContext;
    }


    public void taskToBeExecuted(TaskExecutionContext context) {

    }

    public void taskExecutionVetoed(TaskExecutionContext context) {

    }

    public void taskWasExecuted(TaskExecutionContext context, JobExecutionException jobException) {

    }

    public void taskTriggerFired(TaskExecutionContext context) {

    }

    public boolean vetoTaskExecution(TaskExecutionContext context) {
        return false;
    }

    public void taskTriggerMisfired(Trigger trigger) {

    }

    public void taskTriggerComplete(TaskExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {

    }

}
