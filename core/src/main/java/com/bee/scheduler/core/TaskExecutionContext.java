package com.bee.scheduler.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.quartz.Scheduler;

/**
 * Created by weiwei on 2017/7/8.
 */
public class TaskExecutionContext {
    private Scheduler scheduler;
    private JSONObject taskParam;
    private TaskExecutionLog logger;

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
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
