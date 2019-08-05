package com.bee.scheduler.context.executor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.context.ExpressionPlaceholderHandler;
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

import java.util.Date;

/**
 * @author weiwei
 */
public class TaskExecutor implements Job {
    private Log logger = LogFactory.getLog(TaskExecutor.class);
    private ExpressionPlaceholderHandler expressionPlaceholderHandler = new ExpressionPlaceholderHandler();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();

        TaskExecutionContext taskExecutionContext = TaskExecutionContextUtil.convert(context);
        JSONObject taskParam = taskExecutionContext.getParam();

        try {
            logger.info("开始执行任务:" + jobDetail.getKey());
            logger.info("任务参数:" + taskParam.toString());
            if (expressionPlaceholderHandler.containsExpression(taskParam.toString())) {
                logger.info("任务参数包含表达式,开始计算表达式");
                JSONObject contextVars = new JSONObject();
                contextVars.put("time", new Date());
                contextVars.put("jsonObject", new JSONObject());
                contextVars.put("jsonArray", new JSONArray());
                taskParam = JSON.parseObject(expressionPlaceholderHandler.handle(taskParam.toString(), contextVars));
                taskExecutionContext.setParam(taskParam);
                logger.info("解析后的任务参数:" + taskParam.toString());
            }
            AbstractTaskModule taskModule = TaskModuleRegistry.get(taskExecutionContext.getTaskModuleId());
            if (taskModule == null) {
                throw new TaskModuleNotFountException();
            }
            TaskExecutionResult result = taskModule.run(taskExecutionContext);
            logger.info("任务结果:" + result.getData().toJSONString());
            logger.info("执行任务" + (result.isSuccess() ? "成功" : "失败"));
            TaskExecutionContextUtil.setTaskExecutionResult(context, result);
        } catch (TaskModuleNotFountException e) {
            logger.error("未找到组件: " + taskExecutionContext.getTaskModuleId());
            TaskExecutionContextUtil.setTaskExecutionResult(context, TaskExecutionResult.fail());
            throw new JobExecutionException("未找到组件:" + taskExecutionContext.getTaskModuleId());
        } catch (Throwable e) {
            logger.error("执行任务异常: " + e.getMessage());
            TaskExecutionContextUtil.setTaskExecutionResult(context, TaskExecutionResult.fail());
            throw new JobExecutionException(e);
        }
    }
}