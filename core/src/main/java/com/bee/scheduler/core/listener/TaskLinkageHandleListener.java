package com.bee.scheduler.core.listener;

import com.alibaba.fastjson.JSONArray;
import com.bee.scheduler.core.JobExecutionContextUtil;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLog;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by weiwei on 2017/7/9.
 */
public class TaskLinkageHandleListener extends TaskListenerSupport {
    @Override
    public String getName() {
        return "TaskLinkageHandleListener";
    }

    @Override
    public void taskWasExecuted(TaskExecutionContext context, JobExecutionException jobException) {
        TaskExecutionLog taskLogger = context.getLogger();
        JobExecutionContext jobExecutionContext = context.getJobExecutionContext();
        JSONArray taskLinkageRule = JobExecutionContextUtil.getTaskLinkageRule(jobExecutionContext);

        if (taskLinkageRule != null) {
            taskLogger.warning("触发联动任务:" + taskLinkageRule);
        }
    }
}
