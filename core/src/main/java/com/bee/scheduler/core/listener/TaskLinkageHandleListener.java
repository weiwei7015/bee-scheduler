package com.bee.scheduler.core.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.Constants;
import com.bee.scheduler.core.JobExecutionContextUtil;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLog;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.spi.OperableTrigger;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author weiwei
 */
public class TaskLinkageHandleListener extends TaskListenerSupport {
    @Override
    public String getName() {
        return "TaskLinkageHandleListener";
    }

    private static final SimpleDateFormat time_sequence_date_formater = new SimpleDateFormat("YYYYMMddHHmmssSSS");

    @Override
    public void taskWasExecuted(TaskExecutionContext context, JobExecutionException jobException) {
        TaskExecutionLog taskLogger = context.getLogger();
        JobExecutionContext jobExecutionContext = context.getJobExecutionContext();
        Scheduler scheduler = jobExecutionContext.getScheduler();
        JSONArray taskLinkageRule = JobExecutionContextUtil.getTaskLinkageRule(jobExecutionContext);

        if (taskLinkageRule != null) {
            for (int i = 0; i < taskLinkageRule.size(); i++) {
                Object item = taskLinkageRule.get(i);
                try {
                    if (item instanceof String) {
                        String taskKey = ((String) item);
                        taskLogger.info("触发联动任务:" + taskKey);
                        String[] group$name = StringUtils.split(taskKey, ".");
                        String group = group$name[0];
                        String name = group$name[1];
                        JobKey jobKey = new JobKey(name, group);
                        Trigger trigger = scheduler.getTrigger(new TriggerKey(name, group));
                        JobDataMap jobDataMap = trigger.getJobDataMap();

                        OperableTrigger operableTrigger = (OperableTrigger) TriggerBuilder.newTrigger().withIdentity(jobExecutionContext.getFireInstanceId() + "_" + (i + 1), Constants.TASK_GROUP_LINKAGE).forJob(jobKey).build();
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
                        Trigger trigger = scheduler.getTrigger(new TriggerKey(name, group));
                        JobDataMap jobDataMap = trigger.getJobDataMap();

                        if (StringUtils.isNotBlank(nextLinkageRule)) {
                            if (jobDataMap == null) {
                                jobDataMap = new JobDataMap();
                            }
                            String linkageRule = (String) jobDataMap.get(Constants.JOB_DATA_KEY_TASK_LINKAGE_RULE);

                            if (StringUtils.isBlank(linkageRule) || !StringUtils.equals(nextLinkageRule, linkageRule)) {
                                taskLogger.warning("任务【" + taskKey + "】本次联动执行将采用联动配置：" + nextLinkageRule + "，覆盖默认联动配置：" + linkageRule);
                                jobDataMap.put(Constants.JOB_DATA_KEY_TASK_LINKAGE_RULE, nextLinkageRule);
                            }
                        }

                        OperableTrigger operableTrigger = (OperableTrigger) TriggerBuilder.newTrigger().withIdentity(jobExecutionContext.getFireInstanceId() + "_" + (i + 1), Constants.TASK_GROUP_LINKAGE).forJob(jobKey).build();
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
