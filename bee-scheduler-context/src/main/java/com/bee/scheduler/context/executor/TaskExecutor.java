package com.bee.scheduler.context.executor;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.context.TaskExecutionContextUtil;
import com.bee.scheduler.context.exception.TaskModuleNotFountException;
import com.bee.scheduler.core.AbstractTaskModule;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author weiwei
 */
public class TaskExecutor implements Job {
    private Log logger = LogFactory.getLog(TaskExecutor.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();

        TaskExecutionContext taskExecutionContext = TaskExecutionContextUtil.convert(context);
        JSONObject taskParam = taskExecutionContext.getParam();

        try {
            logger.info("开始执行任务:" + jobDetail.getKey());
            logger.info("任务参数:" + taskParam.toString());
            AbstractTaskModule taskModule = TaskModuleRegistry.get(taskExecutionContext.getTaskModuleId());
            if (taskModule == null) {
                throw new TaskModuleNotFountException();
            }
            TaskExecutionResult result = taskModule.run(taskExecutionContext);
            logger.info("任务结果:" + result.getData().toJSONString());
            logger.info("执行任务" + (result.isSuccess() ? "成功" : "失败"));
            TaskExecutionContextUtil.setTaskExecutionResult(context, result);
        } catch (TaskModuleNotFountException e) {
            logger.error(String.format("未找到组件:%s,FireId:%s", taskExecutionContext.getTaskModuleId(), context.getFireInstanceId()));
            TaskExecutionContextUtil.setTaskExecutionResult(context, TaskExecutionResult.fail());
            throw new JobExecutionException("未找到组件:" + taskExecutionContext.getTaskModuleId());
        } catch (Throwable e) {
            logger.error(String.format("执行任务异常:%s,FireId:%s", e.getMessage(), context.getFireInstanceId()));
            TaskExecutionContextUtil.setTaskExecutionResult(context, TaskExecutionResult.fail());
            throw new JobExecutionException(e);
        }
    }
}