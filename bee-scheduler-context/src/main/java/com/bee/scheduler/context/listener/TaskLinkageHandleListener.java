package com.bee.scheduler.context.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.context.common.Constants;
import com.bee.scheduler.context.common.TaskFiredWay;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLogger;
import com.bee.scheduler.core.TaskExecutionResult;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.spi.OperableTrigger;

import java.util.Calendar;

/**
 * @author weiwei
 */
public class TaskLinkageHandleListener extends AbstractTaskListener {

    @Override
    public String getName() {
        return "TaskLinkageHandleListener";
    }

    @Override
    public void taskWasExecuted(TaskExecutionContext context, TaskExecutionResult result, Scheduler scheduler, JobExecutionException jobException) {
        TaskExecutionLogger taskLogger = context.getLogger();
        JSONArray taskLinkageRule = context.getLinkageRule();

        if (taskLinkageRule != null) {
            for (int i = 0; i < taskLinkageRule.size(); i++) {
                Object item = taskLinkageRule.get(i);
                try {
                    if (item instanceof String) {
                        String taskKey = ((String) item);
                        taskLogger.info("触发联动任务:" + taskKey);
                        String[] group$name = StringUtils.split(taskKey, ".");
                        String group = group$name[0], name = group$name[1];
                        JobKey jobKey = new JobKey(name, group);
                        Trigger trigger = scheduler.getTrigger(new TriggerKey(group + "." + name, TaskFiredWay.SCHEDULE.name()));
                        JobDataMap jobDataMap = trigger.getJobDataMap();

                        OperableTrigger operableTrigger = (OperableTrigger) TriggerBuilder.newTrigger().withIdentity(context.getFireInstanceId() + "_" + (i + 1), TaskFiredWay.LINKAGE.name()).forJob(jobKey).build();
                        operableTrigger.setJobDataMap(jobDataMap);

                        scheduler.scheduleJob(operableTrigger);
                    } else if (item instanceof JSONObject) {
                        JSONObject itemObj = (JSONObject) item;

                        String taskKey = itemObj.getString("task");
                        String nextLinkageRule = itemObj.getString("next");
                        Integer delay = itemObj.getInteger("delay");

                        taskLogger.info("触发联动任务:" + taskKey);

                        String[] group$name = StringUtils.split(taskKey, ".");
                        String group = group$name[0];
                        String name = group$name[1];
                        JobKey jobKey = new JobKey(name, group);
                        Trigger trigger = scheduler.getTrigger(new TriggerKey(group + "." + name, TaskFiredWay.SCHEDULE.name()));
                        JobDataMap jobDataMap = trigger.getJobDataMap();

                        if (StringUtils.isNotBlank(nextLinkageRule)) {
                            if (jobDataMap == null) {
                                jobDataMap = new JobDataMap();
                            }
                            String linkageRule = (String) jobDataMap.get(Constants.TRIGGER_DATA_KEY_TASK_LINKAGE_RULE);

                            if (StringUtils.isBlank(linkageRule) || !StringUtils.equals(nextLinkageRule, linkageRule)) {
                                taskLogger.warning("任务【" + taskKey + "】本次联动执行将采用联动配置：" + nextLinkageRule + "，覆盖默认联动配置：" + linkageRule);
                                jobDataMap.put(Constants.TRIGGER_DATA_KEY_TASK_LINKAGE_RULE, nextLinkageRule);
                            }
                        }

                        OperableTrigger operableTrigger = (OperableTrigger) TriggerBuilder.newTrigger().withIdentity(context.getFireInstanceId() + "_" + (i + 1), TaskFiredWay.LINKAGE.name()).forJob(jobKey).build();
                        operableTrigger.setJobDataMap(jobDataMap);
                        if (delay != null) {
                            taskLogger.info("联动任务【" + taskKey + "】将在" + delay + "ms后开始执行");
                            Calendar startTime = Calendar.getInstance();
                            startTime.add(Calendar.MILLISECOND, delay);
                            operableTrigger.setStartTime(startTime.getTime());
                        }

                        scheduler.scheduleJob(operableTrigger);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
