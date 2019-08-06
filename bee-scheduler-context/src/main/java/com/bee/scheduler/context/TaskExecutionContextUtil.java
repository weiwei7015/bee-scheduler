package com.bee.scheduler.context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.context.common.Constants;
import com.bee.scheduler.core.BasicExecutionResult;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

/**
 * @author weiwei Task执行上下文辅助类
 */
public class TaskExecutionContextUtil {
    public static JobDataMap buildJobDataMapForTask(String taskModuleId, String taskParam, String linkageRule) {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(Constants.TRIGGER_DATA_KEY_TASK_MODULE_ID, taskModuleId);
        dataMap.put(Constants.TRIGGER_DATA_KEY_TASK_PARAM, taskParam);
        dataMap.put(Constants.TRIGGER_DATA_KEY_TASK_LINKAGE_RULE, linkageRule);
        return dataMap;
    }

    public static void setModuleExecutionResult(JobExecutionContext context, BasicExecutionResult result) {
        context.setResult(result);
    }

    public static BasicExecutionResult getModuleExecutionResult(JobExecutionContext context) {
        return (BasicExecutionResult) context.getResult();
    }

    public static JSONObject getTaskParam(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        if (mergedJobDataMap.getString(Constants.TRIGGER_DATA_KEY_TASK_PARAM) == null) {
            return null;
        }
        return JSONObject.parseObject(mergedJobDataMap.getString(Constants.TRIGGER_DATA_KEY_TASK_PARAM));
    }

    public static String getExecutorModuleId(JobExecutionContext context) {
        return context.getMergedJobDataMap().getString(Constants.TRIGGER_DATA_KEY_TASK_MODULE_ID);
    }

    public static JSONArray getLinkageRule(JobExecutionContext context) {
        String linkageRuleString = context.getMergedJobDataMap().getString(Constants.TRIGGER_DATA_KEY_TASK_LINKAGE_RULE);
        if (StringUtils.isBlank(linkageRuleString)) {
            return null;
        }
        return JSONObject.parseArray(linkageRuleString);
    }
}
