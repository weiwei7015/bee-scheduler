package com.bee.scheduler.context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLogger;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author weiwei Job执行上下文辅助类
 */
@SuppressWarnings("unchecked")
public class TaskExecutionContextUtil {
    public static JobDataMap buildJobDataMapForTask(String taskModuleId, String taskParam, String linkageRule) {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(Constants.JOB_DATA_KEY_TASK_MODULE_ID, taskModuleId);
        dataMap.put(Constants.JOB_DATA_KEY_TASK_PARAM, taskParam);
        dataMap.put(Constants.JOB_DATA_KEY_TASK_LINKAGE_RULE, linkageRule);
        return dataMap;
    }

    public static TaskExecutionContext convert(JobExecutionContext context) {
        JobKey jobKey = context.getJobDetail().getKey();
        Trigger trigger = context.getTrigger();
        Scheduler scheduler = context.getScheduler();

        TaskExecutionContext taskExecutionContext = new TaskExecutionContext();
        taskExecutionContext.setTaskModuleId(getTaskModuleId(context));
        taskExecutionContext.setParam(getTaskParam(context));
        taskExecutionContext.setLinkageRule(getTaskLinkageRule(context));
        taskExecutionContext.setSchedulerName(jobKey.getGroup());
        try {
            taskExecutionContext.setSchedulerInstanceId(scheduler.getSchedulerInstanceId());
        } catch (SchedulerException e) {
            taskExecutionContext.setSchedulerInstanceId("unknown");
        }
        taskExecutionContext.setJobGroup(jobKey.getGroup());
        taskExecutionContext.setJobName(jobKey.getName());
        taskExecutionContext.setTriggerGroup(trigger.getKey().getGroup());
        taskExecutionContext.setTriggerName(trigger.getKey().getName());
        taskExecutionContext.setFireInstanceId(context.getFireInstanceId());
        taskExecutionContext.setFireTime(context.getFireTime());
        taskExecutionContext.setJobRunTime(context.getJobRunTime());
        taskExecutionContext.setRefireCount(context.getRefireCount());
        taskExecutionContext.setPreviousFireTime(context.getPreviousFireTime());
        taskExecutionContext.setLogger(getLogger(context));
        return taskExecutionContext;
    }

    //获取任务参数
    private static JSONObject getTaskParam(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        if (mergedJobDataMap.getString(Constants.JOB_DATA_KEY_TASK_PARAM) == null) {
            return new JSONObject();
        }
        return JSONObject.parseObject(mergedJobDataMap.getString(Constants.JOB_DATA_KEY_TASK_PARAM));
    }

    //获取任务组件ID
    private static String getTaskModuleId(JobExecutionContext context) {
        return context.getMergedJobDataMap().getString(Constants.JOB_DATA_KEY_TASK_MODULE_ID);
    }

    //获取任务联动规则
    private static JSONArray getTaskLinkageRule(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        if (StringUtils.isBlank(mergedJobDataMap.getString(Constants.JOB_DATA_KEY_TASK_LINKAGE_RULE))) {
            return null;
        }
        return JSONObject.parseArray(mergedJobDataMap.getString(Constants.JOB_DATA_KEY_TASK_LINKAGE_RULE));
    }

    private static TaskExecutionLogger getLogger(JobExecutionContext context) {
        Map<String, Object> contextResultMap = getContextResultMap(context);
        return (TaskExecutionLogger) contextResultMap.computeIfAbsent(Constants.JOB_EXEC_CONTEXT_RESULT_MAP_KEY_TASK_LOG, k -> new TaskExecutionLogger());
    }

    private static Map<String, Object> getContextResultMap(JobExecutionContext context) {
        Map<String, Object> result = (Map<String, Object>) context.getResult();
        if (result == null) {
            result = new HashMap<>();
            context.setResult(result);
        }
        return result;
    }
}
