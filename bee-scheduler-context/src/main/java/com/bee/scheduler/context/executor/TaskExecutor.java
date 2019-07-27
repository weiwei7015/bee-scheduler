package com.bee.scheduler.context.executor;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.context.TaskExecutionContextUtil;
import com.bee.scheduler.core.AbstractTaskModule;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLogger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author weiwei
 */
public class TaskExecutor implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        boolean execSuccess = true;
        JobDetail jobDetail = context.getJobDetail();

        TaskExecutionContext taskExecutionContext = TaskExecutionContextUtil.convert(context);
        TaskExecutionLogger taskLogger = taskExecutionContext.getLogger();
        JSONObject taskParam = taskExecutionContext.getParam();
        try {
            taskLogger.info("开始执行任务 -> " + jobDetail.getKey());
            taskLogger.info("任务参数 -> " + taskParam.toString());
            AbstractTaskModule taskModule = TaskModuleRegistry.get(taskExecutionContext.getTaskModuleId());
            execSuccess = taskModule.run(taskExecutionContext);
        } catch (Exception e) {
            execSuccess = false;
            taskLogger.error("执行任务异常 -> " + e.getMessage(), e);
            throw new JobExecutionException(e);
        } finally {
            taskLogger.info("执行任务结束 -> " + (execSuccess ? "成功" : "失败"));
        }
    }
}