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
        JSONArray taskLinkageRule = TaskExecutionContextUtil.getLinkageRule(context);
        ExecutionResult taskModuleExecutionResult = TaskExecutionContextUtil.getModuleExecutionResult(context);

        if (taskLinkageRule == null) {
            return;
        }
        if (jobException != null) {
            logger.warn("任务执行失败，联动任务已取消");
            return;
        }

        try {
            logger.info("解析联动配置: " + taskLinkageRule);
            for (int i = 0; i < taskLinkageRule.size(); i++) {
                Object item = taskLinkageRule.get(i);
                logger.info("处理联动配置: " + (i + 1));
                if (item instanceof String) {
                    String[] group$name = StringUtils.split(((String) item), ".");
                    String taskGroup = group$name[0], taskName = group$name[1];
                    TriggerKey fireTriggerKey = new TriggerKey(context.getFireInstanceId() + "_" + (i + 1), TaskFiredWay.LINKAGE.name());
                    fireExistTask(context, fireTriggerKey, taskGroup, taskName, null);
                } else if (item instanceof JSONObject) {
                    JSONObject linkageRule = (JSONObject) item;
                    if (expressionPlaceholderHandler.containsExpression(linkageRule.toString())) {
                        logger.info("联动包含表达式,开始计算表达式");
                        //联动规则解析
                        JobKey mainJobKey = context.getJobDetail().getKey();
                        JSONObject contextVars = new JSONObject();
                        contextVars.put("taskGroup", mainJobKey.getGroup());
                        contextVars.put("taskName", mainJobKey.getName());
                        contextVars.put("time", new Date());
                        contextVars.put("jsonObject", new JSONObject());
                        contextVars.put("jsonArray", new JSONArray());
                        if (taskModuleExecutionResult.getData() != null) {
                            contextVars.putAll(taskModuleExecutionResult.getData());
                        }
                        linkageRule = JSONObject.parseObject(expressionPlaceholderHandler.handle(linkageRule.toString(), contextVars));
                        logger.info("解析后的任务参数:" + linkageRule);
                    }

                    Long delay = linkageRule.getLong("delay");
                    Object task = linkageRule.get("task");
                    Boolean condition = linkageRule.getBoolean("condition");

                    if (condition == null || !condition) {
                        logger.info("condition结算结果为false，取消执行联动任务：" + (i + 1));
                        continue;
                    }

                    if (task instanceof String) {
                        String[] group$name = StringUtils.split(((String) task), ".");
                        String taskGroup = group$name[0], taskName = group$name[1];
                        TriggerKey fireTriggerKey = new TriggerKey(context.getFireInstanceId() + "_" + (i + 1), TaskFiredWay.LINKAGE.name());
                        fireExistTask(context, fireTriggerKey, taskGroup, taskName, delay);
                    } else if (task instanceof JSONObject) {
                        JSONObject taskConfig = (JSONObject) task;
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
