package com.bee.scheduler.context.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.context.ExpressionPlaceholderHandler;
import com.bee.scheduler.context.TaskExecutionContextUtil;
import com.bee.scheduler.context.common.Constants;
import com.bee.scheduler.context.exception.ExecutorModuleNotFountException;
import com.bee.scheduler.core.ExecutionException;
import com.bee.scheduler.core.ExecutionResult;
import com.bee.scheduler.core.ExecutorModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;

import java.util.Date;

/**
 * @author weiwei
 */
public class TaskExecutorProxy implements Job {
    private Log logger = LogFactory.getLog(TaskExecutorProxy.class);
    private ExpressionPlaceholderHandler expressionPlaceholderHandler = new ExpressionPlaceholderHandler();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("开始执行任务 -> " + context.getJobDetail().getKey());
        ExecutionResult result = null;
        try {
            logger.info("任务参数 -> " + TaskExecutionContextUtil.getTaskParam(context));
            TaskExecutionContext taskExecutionContext = prepareExecutionContext(context);
            ExecutorModule taskModule = TaskModuleRegistry.get(taskExecutionContext.getExecutorModuleId());
            if (taskModule == null) {
                throw new ExecutorModuleNotFountException(taskExecutionContext.getExecutorModuleId());
            }
            result = taskModule.exec(taskExecutionContext);
            logger.info("执行任务" + (result.isSuccess() ? "成功" : "失败"));
            TaskExecutionContextUtil.setModuleExecutionResult(context, result);
        } catch (ExecutorModuleNotFountException e) {
            logger.error("未找到组件: " + e.getExecutorModuleId());
            result = ExecutionResult.fail();
            TaskExecutionContextUtil.setModuleExecutionResult(context, result);
            throw new JobExecutionException("未找到组件:" + e.getExecutorModuleId());
        } catch (ExecutionException e) {
            logger.error("任务执行失败,原因:" + e.getMessage());
            result = ExecutionResult.fail();
            TaskExecutionContextUtil.setModuleExecutionResult(context, result);
            throw new JobExecutionException(e);
        } catch (Throwable e) {
            logger.error("任务执行异常", e);
            result = ExecutionResult.fail();
            TaskExecutionContextUtil.setModuleExecutionResult(context, result);
            throw new JobExecutionException(e);
        } finally {
            if (result != null) {
                logger.info("任务结果 -> isSuccess: " + result.isSuccess() + ", data: " + result.getData().toJSONString());
            }
        }
    }

    private TaskExecutionContext prepareExecutionContext(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        //ModuleId
        String executorModuleId = mergedJobDataMap.getString(Constants.TRIGGER_DATA_KEY_TASK_MODULE_ID);
        //任务参数
        String paramString = mergedJobDataMap.getString(Constants.TRIGGER_DATA_KEY_TASK_PARAM);
        JSONObject param = null;
        if (StringUtils.isNotBlank(paramString)) {
            if (expressionPlaceholderHandler.containsExpression(paramString)) {
                logger.info("任务参数包含表达式,开始计算表达式");
                JobKey jobKey = context.getJobDetail().getKey();
                //全局参数
                JSONObject vars = new JSONObject();
                vars.put("taskGroup", jobKey.getGroup());
                vars.put("taskName", jobKey.getName());
                vars.put("taskFireId", context.getFireInstanceId());
                vars.put("time", new Date());
                vars.put("JsonObject", new JSONObject());
                vars.put("JsonArray", new JSONArray());
                paramString = expressionPlaceholderHandler.handle(paramString, vars);
                logger.info("解析后的任务参数:" + paramString);
            }
            param = JSONObject.parseObject(paramString);
        }
        //联动规则
        String linkageRuleString = mergedJobDataMap.getString(Constants.TRIGGER_DATA_KEY_TASK_LINKAGE_RULE);
        JSONArray linkageRule = StringUtils.isBlank(linkageRuleString) ? null : JSONObject.parseArray(linkageRuleString);
        return new TaskExecutionContext(context, executorModuleId, param, linkageRule);
    }
}