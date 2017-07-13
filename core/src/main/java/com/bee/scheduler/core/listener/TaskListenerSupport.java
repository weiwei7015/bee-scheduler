package com.bee.scheduler.core.listener;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.JobExecutionContextUtil;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLog;
import org.quartz.*;
import org.quartz.listeners.JobListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by weiwei
 */
public abstract class TaskListenerSupport implements JobListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        JSONObject taskParam = JobExecutionContextUtil.getTaskParam(context);
        TaskExecutionLog taskLogger = new TaskExecutionLog(context);

        TaskExecutionContext taskExecutionContext = new TaskExecutionContext();
        taskExecutionContext.setJobExecutionContext(context);
        taskExecutionContext.setTaskParam(taskParam);
        taskExecutionContext.setLogger(taskLogger);

        taskToBeExecuted(taskExecutionContext);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        JSONObject taskParam = JobExecutionContextUtil.getTaskParam(context);
        TaskExecutionLog taskLogger = new TaskExecutionLog(context);

        TaskExecutionContext taskExecutionContext = new TaskExecutionContext();
        taskExecutionContext.setJobExecutionContext(context);
        taskExecutionContext.setTaskParam(taskParam);
        taskExecutionContext.setLogger(taskLogger);

        taskExecutionVetoed(taskExecutionContext);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        JSONObject taskParam = JobExecutionContextUtil.getTaskParam(context);
        TaskExecutionLog taskLogger = new TaskExecutionLog(context);

        TaskExecutionContext taskExecutionContext = new TaskExecutionContext();
        taskExecutionContext.setJobExecutionContext(context);
        taskExecutionContext.setTaskParam(taskParam);
        taskExecutionContext.setLogger(taskLogger);

        taskWasExecuted(taskExecutionContext, jobException);
    }


    public void taskToBeExecuted(TaskExecutionContext context) {

    }

    public void taskExecutionVetoed(TaskExecutionContext context) {

    }

    public void taskWasExecuted(TaskExecutionContext context, JobExecutionException jobException) {

    }
}
