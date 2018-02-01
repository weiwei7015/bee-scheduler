package com.bee.scheduler.core;

import com.alibaba.fastjson.JSONObject;
import org.quartz.JobExecutionContext;

/**
 * @author weiwei
 */
public class TaskExecutionContext {
    private JobExecutionContext jobExecutionContext;
    private JSONObject taskParam;
    private TaskExecutionLog logger;

    public JobExecutionContext getJobExecutionContext() {
        return jobExecutionContext;
    }

    public void setJobExecutionContext(JobExecutionContext jobExecutionContext) {
        this.jobExecutionContext = jobExecutionContext;
    }

    public JSONObject getTaskParam() {
        return taskParam;
    }

    public void setTaskParam(JSONObject taskParam) {
        this.taskParam = taskParam;
    }

    public TaskExecutionLog getLogger() {
        return logger;
    }

    public void setLogger(TaskExecutionLog logger) {
        this.logger = logger;
    }
}
