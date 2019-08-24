package com.bee.scheduler.context.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.context.ExpressionPlaceholderHandler;
import com.bee.scheduler.context.TaskExecutionContextUtil;
import com.bee.scheduler.context.common.Constants;
import com.bee.scheduler.context.common.TaskFiredWay;
import com.bee.scheduler.context.common.TaskSpecialGroup;
import com.bee.scheduler.context.listener.support.LinkageRuleResolver;
import com.bee.scheduler.context.task.TaskExecutorProxy;
import com.bee.scheduler.core.ExecutionResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author weiwei
 */
public class TaskLinkageHandleListener extends TaskListenerSupport {
    private Log logger = LogFactory.getLog(TaskLinkageHandleListener.class);
    private ExpressionPlaceholderHandler expressionPlaceholderHandler = new ExpressionPlaceholderHandler();
    private LinkageRuleResolver linkageRuleResolver = new LinkageRuleResolver();

    @Override
    public String getName() {
        return "TaskLinkageHandleListener";
    }


    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        JSONArray taskLinkageRules = TaskExecutionContextUtil.getLinkageRule(context);

        if (taskLinkageRules == null) {
            return;
        }
        if (jobException != null) {
            logger.warn("任务执行失败，联动任务已取消");
            return;
        }

        try {
            logger.info("解析联动配置: " + taskLinkageRules);
            for (int i = 0; i < taskLinkageRules.size(); i++) {
                Object item = taskLinkageRules.get(i);
                logger.info("处理联动配置: " + (i + 1));
                if (item instanceof String) {
                    String[] group$name = StringUtils.split(((String) item), ".");
                    String taskGroup = group$name[0], taskName = group$name[1];
                    TriggerKey fireTriggerKey = new TriggerKey(context.getFireInstanceId() + "_" + (i + 1), TaskFiredWay.LINKAGE.name());
                    fireExistTask(context, fireTriggerKey, taskGroup, taskName, null);
                } else if (item instanceof JSONObject) {
                    JSONObject linkageRule = (JSONObject) item;

                    //计算condition
                    String conditionEl = linkageRule.getString("condition");
                    if (StringUtils.isNotBlank(conditionEl)) {
                        JSONObject contextVars = prepareVariables(context);
                        Boolean conditionResult = expressionPlaceholderHandler.compute(conditionEl, contextVars, Boolean.class);
                        linkageRule.put("condition", conditionResult);
                        logger.info("condition:[" + conditionEl + "] -> " + conditionResult);
                        if (!conditionResult) {
                            logger.info("condition结算结果为false，取消执行联动任务：" + (i + 1));
                            continue;
                        }
                    }

                    //计算export
                    String exportEl = linkageRule.getString("export");
                    if (StringUtils.isNotBlank(exportEl)) {
                        JSONObject contextVars = prepareVariables(context);
                        Map exportResult = expressionPlaceholderHandler.compute(exportEl, contextVars, Map.class);
                        linkageRule.put("export", exportResult);
                        logger.info("export:[ " + exportEl + " ] -> " + exportResult);
                    }

                    Long delay = linkageRule.getLong("delay");
                    Object task = linkageRule.get("task");
                    JSONObject export = linkageRule.getJSONObject("export");

                    if (task instanceof String) {
                        String[] group$name = StringUtils.split(((String) task), ".");
                        String taskGroup = group$name[0], taskName = group$name[1];
                        TriggerKey fireTriggerKey = new TriggerKey(context.getFireInstanceId() + "_" + (i + 1), TaskFiredWay.LINKAGE.name());
                        fireExistTask(context, fireTriggerKey, taskGroup, taskName, delay);
                    } else if (task instanceof JSONObject) {
                        JSONObject taskConfig = (JSONObject) task;

                        String taskConfigParams = taskConfig.getString("params");
                        if (expressionPlaceholderHandler.containsExpression(taskConfigParams)) {
                            logger.info("联动任务参数包含表达式,开始计算表达式...");
                            //联动规则解析
                            JSONObject contextVars = prepareVariables(context);
                            if (export != null) {
                                contextVars.putAll(export);
                            }
                            taskConfigParams = expressionPlaceholderHandler.handle(taskConfigParams, contextVars);
                            taskConfig.put("params", taskConfigParams);
                            logger.info("解析后的任务参数:" + taskConfigParams);
                        }

                        String nextTaskGroup = TaskSpecialGroup.LINKTMP.name();
                        String nextTaskName = context.getFireInstanceId() + "_" + (i + 1);
                        scheduleNewTask(context, taskConfig, nextTaskGroup, nextTaskName, delay);
                    } else {
                        logger.error("无效的联动配置:" + task);
                    }
                } else {
                    logger.error("无效的联动配置:" + item);
                }
            }
            logger.info("联动任务处理完成");
        } catch (Exception e) {
            logger.error("处理联动任务异常", e);
        }
    }

    private JSONObject prepareVariables(JobExecutionContext context) {
        JobKey mainJobKey = context.getJobDetail().getKey();
        ExecutionResult taskModuleExecutionResult = TaskExecutionContextUtil.getModuleExecutionResult(context);
        JSONObject vars = new JSONObject();
        vars.put("mainTaskFireId", mainJobKey.getName());
        vars.put("mainTaskGroup", mainJobKey.getGroup());
        vars.put("mainTaskName", mainJobKey.getName());
        vars.put("mainTaskResult", taskModuleExecutionResult);
        vars.put("time", new Date());
        vars.put("JsonObject", new JSONObject());
        vars.put("JsonArray", new JSONArray());
        return vars;
    }

    private void scheduleNewTask(JobExecutionContext context, JSONObject taskConfig, String taskGroup, String taskName, Long delay) throws SchedulerException {
        Scheduler scheduler = context.getScheduler();

        String executorModule = taskConfig.getString("taskModule");
        String taskParams = taskConfig.getString("params");
        String linkageRule = taskConfig.getString("linkageRule");

        JobDetail jobDetail = JobBuilder.newJob(TaskExecutorProxy.class).withIdentity(taskName, taskGroup).build();
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(Constants.TRIGGER_DATA_KEY_TASK_MODULE_ID, executorModule);
        jobDataMap.put(Constants.TRIGGER_DATA_KEY_TASK_PARAM, taskParams);
        jobDataMap.put(Constants.TRIGGER_DATA_KEY_TASK_LINKAGE_RULE, linkageRule);

        TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(taskGroup + "." + taskName, TaskFiredWay.LINKAGE.name()).usingJobData(jobDataMap);
        if (delay != null) {
            Calendar startTime = Calendar.getInstance();
            startTime.add(Calendar.MILLISECOND, delay.intValue());
            triggerBuilder.startAt(startTime.getTime());
            scheduler.scheduleJob(jobDetail, triggerBuilder.build());
            logger.info("联动任务[ " + taskGroup + "." + taskName + " ]将在 " + delay + " ms后开始执行");
        } else {
            scheduler.scheduleJob(jobDetail, triggerBuilder.build());
            logger.info("已触发任务: " + taskGroup + "." + taskName);
        }
    }

    private void fireExistTask(JobExecutionContext context, TriggerKey fireTriggerKey, String taskGroup, String taskName, Long delay) throws SchedulerException {
        Scheduler scheduler = context.getScheduler();
        JobKey targetTaskJobKey = new JobKey(taskName, taskGroup);
        TriggerKey targetTaskTriggerKey = new TriggerKey(taskGroup + "." + taskName, TaskFiredWay.SCHEDULE.name());
        Trigger targetTaskTrigger = scheduler.getTrigger(targetTaskTriggerKey);
        if (targetTaskTrigger == null) {
            logger.error("任务不存在: " + taskGroup + "." + taskName);
            return;
        }
        JobDataMap targetTaskTriggerDataMap = targetTaskTrigger.getJobDataMap();
        TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(fireTriggerKey).usingJobData(targetTaskTriggerDataMap).forJob(targetTaskJobKey);
        if (delay != null) {
            Calendar startTime = Calendar.getInstance();
            startTime.add(Calendar.MILLISECOND, delay.intValue());
            triggerBuilder.startAt(startTime.getTime());
            scheduler.scheduleJob(triggerBuilder.build());
            logger.info("联动任务[ " + taskGroup + "." + taskName + " ]将在 " + delay + " ms后开始执行");
        } else {
            scheduler.scheduleJob(triggerBuilder.build());
            logger.info("已触发任务: " + taskGroup + "." + taskName);
        }
    }
}
