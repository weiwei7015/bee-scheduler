package com.bee.scheduler.context.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.context.ExpressionPlaceholderHandler;
import com.bee.scheduler.context.TaskExecutionContextUtil;
import com.bee.scheduler.context.common.Constants;
import com.bee.scheduler.context.common.TaskFiredWay;
import com.bee.scheduler.context.common.TaskSpecialGroup;
import com.bee.scheduler.context.executor.TaskExecutorProxy;
import com.bee.scheduler.context.listener.support.LinkageRuleResolver;
import com.bee.scheduler.context.listener.support.ResolvedLinkageRule;
import com.bee.scheduler.core.BasicExecutionResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.springframework.context.ApplicationContextAware;

import java.util.Calendar;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author weiwei
 */
public class TaskLinkageHandleListener extends TaskListenerSupport implements ApplicationContextAware {
    private Log logger = LogFactory.getLog(TaskLinkageHandleListener.class);
    private ExpressionPlaceholderHandler expressionPlaceholderHandler = new ExpressionPlaceholderHandler();
    private LinkageRuleResolver linkageRuleResolver = new LinkageRuleResolver();

    @Override
    public String getName() {
        return "TaskLinkageHandleListener";
    }


    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        Scheduler scheduler = context.getScheduler();
        JSONArray taskLinkageRule = TaskExecutionContextUtil.getLinkageRule(context);
        BasicExecutionResult taskModuleExecutionResult = TaskExecutionContextUtil.getModuleExecutionResult(context);

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
                logger.info("处理联动配置" + (i + 1) + ": " + item.toString());
                if (item instanceof String) {
                    String[] group$name = StringUtils.split(((String) item), ".");
                    String group = group$name[0], name = group$name[1];

                    JobKey nextJobKey = new JobKey(name, group);
                    TriggerKey nextTriggerKey = new TriggerKey(group + "." + name, TaskFiredWay.SCHEDULE.name());
                    Trigger nextTrigger = scheduler.getTrigger(nextTriggerKey);
                    JobDataMap nextJobDataMap = nextTrigger.getJobDataMap();

                    TriggerKey linkageTriggerKey = new TriggerKey(context.getFireInstanceId() + "_" + (i + 1), TaskFiredWay.LINKAGE.name());
                    TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(linkageTriggerKey).usingJobData(nextJobDataMap).forJob(nextJobKey);
                    scheduler.scheduleJob(triggerBuilder.build());

                    logger.info("已触发联动任务: " + group + "." + name);
                } else if (item instanceof JSONObject) {
                    //联动规则解析
                    JSONObject contextVars = new JSONObject();
                    if (taskModuleExecutionResult.getData() != null) {
                        contextVars.putAll(taskModuleExecutionResult.getData());
                    }
                    ResolvedLinkageRule linkageRule = linkageRuleResolver.resolve((JSONObject) item, contextVars);
                    if (!linkageRule.getCondition()) {
                        logger.info("condition[ " + linkageRule.getConditionEl() + " ]计算结果false，取消联动");
                        return;
                    }
                    if (linkageRule.getExports() != null) {
                        contextVars.putAll(linkageRule.getExports());
                    }
                    String taskKey = linkageRule.getTaskGroup() + "." + linkageRule.getTaskName();

                    if (linkageRule.getMode() == ResolvedLinkageRule.Mode.Trigger) {
                        JobKey nextTaskJobKey = new JobKey(linkageRule.getTaskName(), linkageRule.getTaskGroup());
                        Trigger nextTaskTrigger = scheduler.getTrigger(new TriggerKey(taskKey, TaskFiredWay.SCHEDULE.name()));
                        JobDataMap nextTaskJobDataMap = nextTaskTrigger.getJobDataMap();

                        TriggerBuilder triggerBuilder = newTrigger().withIdentity(context.getFireInstanceId() + "_" + (i + 1), TaskFiredWay.LINKAGE.name()).usingJobData(nextTaskJobDataMap).forJob(nextTaskJobKey);
                        if (linkageRule.getDelay() != null) {
                            Calendar startTime = Calendar.getInstance();
                            startTime.add(Calendar.MILLISECOND, linkageRule.getDelay());
                            triggerBuilder.startAt(startTime.getTime());
                            scheduler.scheduleJob(triggerBuilder.build());
                            logger.info("联动任务[ " + taskKey + " ]将在 " + linkageRule.getDelay() + " ms后开始执行");
                        } else {
                            scheduler.scheduleJob(triggerBuilder.build());
                            logger.info("联动任务[ " + taskKey + " ]已触发");
                        }
                    } else {
                        ResolvedLinkageRule.LinkageTaskConfig linkageTaskConfig = linkageRule.getLinkageTaskConfig();

                        if (expressionPlaceholderHandler.containsExpression(linkageTaskConfig.getParams().toString())) {
                            logger.info("任务参数包含表达式,开始计算表达式");
                            String resolveParams = expressionPlaceholderHandler.handle(linkageTaskConfig.getParams().toString(), contextVars);
                            linkageTaskConfig.setParams(JSONObject.parseObject(resolveParams));
                            logger.info("解析后的任务参数:" + linkageTaskConfig.getParams());
                        }

                        String group = TaskSpecialGroup.LINKTMP.name();
                        String name = context.getFireInstanceId() + "_" + (i + 1);
                        JobDetail jobDetail = JobBuilder.newJob(TaskExecutorProxy.class).withIdentity(name, group).build();
                        JobDataMap jobDataMap = new JobDataMap();
                        jobDataMap.put(Constants.TRIGGER_DATA_KEY_TASK_MODULE_ID, linkageTaskConfig.getTaskModule());
                        jobDataMap.put(Constants.TRIGGER_DATA_KEY_TASK_PARAM, linkageTaskConfig.getParams());
                        jobDataMap.put(Constants.TRIGGER_DATA_KEY_TASK_LINKAGE_RULE, linkageTaskConfig.getLinkageRule());

                        TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(group + "." + name, TaskFiredWay.LINKAGE.name()).usingJobData(jobDataMap);
                        if (linkageRule.getDelay() != null) {
                            Calendar startTime = Calendar.getInstance();
                            startTime.add(Calendar.MILLISECOND, linkageRule.getDelay());
                            triggerBuilder.startAt(startTime.getTime());
                        }
                        scheduler.scheduleJob(jobDetail, triggerBuilder.build());
                        logger.info("联动任务[ " + taskKey + " ]将在 " + linkageRule.getDelay() + " ms后开始执行");
                    }
                }
            }
            logger.info("联动任务处理完成");
        } catch (Exception e) {
            logger.error("处理联动任务异常", e);
        }
    }


}
