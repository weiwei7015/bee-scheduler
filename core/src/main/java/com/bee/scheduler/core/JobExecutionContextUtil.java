package com.bee.scheduler.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author weiwei Job执行上下文辅助类
 */
@SuppressWarnings("unchecked")
public class JobExecutionContextUtil {
    /**
     * 获取任务参数
     */
    public static JSONObject getTaskParam(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        if (mergedJobDataMap.getString(Constants.JOB_DATA_KEY_TASK_PARAM) == null) {
            return new JSONObject();
        }
        return JSONObject.parseObject(mergedJobDataMap.getString(Constants.JOB_DATA_KEY_TASK_PARAM));
    }

    /**
     * 获取任务联动规则
     */
    public static JSONArray getTaskLinkageRule(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        if (StringUtils.isBlank(mergedJobDataMap.getString(Constants.JOB_DATA_KEY_TASK_LINKAGE_RULE))) {
            return null;
        }
        return JSONObject.parseArray(mergedJobDataMap.getString(Constants.JOB_DATA_KEY_TASK_LINKAGE_RULE));
    }

    public static Map<String, Object> getContextResultMap(JobExecutionContext context) {
        Map<String, Object> result = (Map<String, Object>) context.getResult();
        if (result == null) {
            result = new HashMap<>();
            context.setResult(result);
        }
        return result;
    }

}
