package com.bee.scheduler.context.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.context.TaskExecutionContextUtil;
import com.bee.scheduler.context.common.TaskFiredWay;
import com.bee.scheduler.context.common.TaskSpecialGroup;
import com.bee.scheduler.context.executor.TaskExecutor;
import com.bee.scheduler.context.listener.support.ExpressionPlaceholderHandler;
import com.bee.scheduler.context.listener.support.LinkageRuleResolver;
import com.bee.scheduler.context.listener.support.ResolvedLinkageRule;
import com.bee.scheduler.context.model.TaskConfig;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.spi.OperableTrigger;

import java.util.Calendar;
import java.util.Date;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author weiwei
 */
public class TaskLinkageHandleListener extends AbstractTaskListener {
    private Log logger = LogFactory.getLog(TaskLinkageHandleListener.class);
    private ExpressionPlaceholderHandler expressionPlaceholderHandler = new ExpressionPlaceholderHandler();
    private LinkageRuleResolver linkageRuleResolver = new LinkageRuleResolver();

    @Override
    public String getName() {
        return "TaskLinkageHandleListener";
    }

    @Override
    public void taskWasExecuted(TaskExecutionContext context, TaskExecutionResult result, Scheduler scheduler, JobExecutionException jobException) {
        JSONArray taskLinkageRule = context.getLinkageRule();

        if (!result.isSuccess()) {
            return;
        }

        if (taskLinkageRule != null) {
            Date now = new Date();
            for (int i = 0; i < taskLinkageRule.size(); i++) {
                Object item = taskLinkageRule.get(i);
                try {
                    if (item instanceof String) {
                        String taskKey = ((String) item);
                        logger.info("触发联动任务:" + taskKey);
                        String[] group$name = StringUtils.split(taskKey, ".");
                        String group = group$name[0], name = group$name[1];
                        JobKey jobKey = new JobKey(name, group);
                        Trigger trigger = scheduler.getTrigger(new TriggerKey(group + "." + name, TaskFiredWay.SCHEDULE.name()));
                        JobDataMap jobDataMap = trigger.getJobDataMap();

                        OperableTrigger operableTrigger = (OperableTrigger) TriggerBuilder.newTrigger().withIdentity(context.getFireInstanceId() + "_" + (i + 1), TaskFiredWay.LINKAGE.name()).forJob(jobKey).build();
                        operableTrigger.setJobDataMap(jobDataMap);

                        scheduler.scheduleJob(operableTrigger);
                    } else if (item instanceof JSONObject) {
                        //联动规则解析
                        JSONObject contextVars = new JSONObject();
                        if (result.getData() != null) {
                            contextVars.putAll(result.getData());
                        }
                        contextVars.put("time", now);
                        contextVars.putAll(result.getData());
                        ResolvedLinkageRule linkageRule = linkageRuleResolver.resolve((JSONObject) item, contextVars);
                        if (linkageRule.getExports() != null) {
                            contextVars.putAll(linkageRule.getExports());
                        }
                        //触发联动任务
                        if (linkageRule.getMode() == ResolvedLinkageRule.Mode.Trigger) {
                            String taskKey = linkageRule.getTaskGroup() + "." + linkageRule.getTaskName();
                            logger.info("触发联动任务:" + taskKey);

                            JobKey taskJobKey = new JobKey(linkageRule.getTaskName(), linkageRule.getTaskGroup());
                            Trigger taskTrigger = scheduler.getTrigger(new TriggerKey(taskKey, TaskFiredWay.SCHEDULE.name()));
                            JobDataMap taskTriggerDataMap = taskTrigger.getJobDataMap();

                            TriggerBuilder triggerBuilder = newTrigger().withIdentity(context.getFireInstanceId() + "_" + (i + 1), TaskFiredWay.LINKAGE.name()).usingJobData(taskTriggerDataMap).forJob(taskJobKey);
                            if (linkageRule.getDelay() != null) {
                                logger.info("联动任务【" + taskKey + "】将在" + linkageRule.getDelay() + "ms后开始执行");
                                Calendar startTime = Calendar.getInstance();
                                startTime.add(Calendar.MILLISECOND, linkageRule.getDelay());
                                triggerBuilder.startAt(startTime.getTime());
                            }
                            scheduler.scheduleJob(triggerBuilder.build());
                        } else {
                            if (!linkageRule.getCondition()) {
                                logger.info("condition计算结果false，取消联动");
                                return;
                            }

                            TaskConfig taskConfig = linkageRule.getTaskConfig();
                            String group = TaskSpecialGroup.LINKTMP.name();
                            String name = context.getFireInstanceId() + "_" + (i + 1);

                            JobDetail jobDetail = JobBuilder.newJob(TaskExecutor.class).withIdentity(name, group).build();

                            taskConfig.setParams(expressionPlaceholderHandler.handle(taskConfig.getParams(), contextVars));

                            JobDataMap jobDataMap = TaskExecutionContextUtil.buildJobDataMapForTask(taskConfig.getTaskModule(), taskConfig.getParams(), taskConfig.getLinkageRule());
                            TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(group + "." + name, TaskFiredWay.LINKAGE.name()).usingJobData(jobDataMap);

                            if (linkageRule.getDelay() != null) {
                                Calendar startTime = Calendar.getInstance();
                                startTime.add(Calendar.MILLISECOND, linkageRule.getDelay());
                                triggerBuilder.startAt(startTime.getTime());
                            }
                            scheduler.scheduleJob(jobDetail, triggerBuilder.build());
                        }
                    }
                } catch (Exception e) {
                    logger.error("联动任务执行异常,TaskLinkageRule:" + taskLinkageRule, e);
                }
            }
        }
    }
}
