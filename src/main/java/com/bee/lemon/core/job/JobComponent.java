package com.bee.lemon.core.job;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.bee.lemon.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

/**
 * @author weiwei 任务组件接口
 */
public abstract class JobComponent implements Job {
    @JSONField(deserialize = false, serialize = false)
    public Log log = LogFactory.getLog(this.getClass());

    /**
     * 名称
     */
    public abstract String getName();

    /**
     * 描述
     */
    public abstract String getDescription();

    /**
     * 参数模板：json格式
     */
    public abstract String getParamTemplate();

    /**
     * 版本号
     */
    public abstract String getVersion();

    /**
     * 作者
     */
    public abstract String getAuthor();

    /**
     * 获取任务参数
     */
    public JSONObject getTaskParam(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        if (mergedJobDataMap.getString(Constants.TASK_PARAM_JOB_DATA_KEY) == null) {
            return new JSONObject();
        }
        return JSONObject.parseObject(mergedJobDataMap.getString(Constants.TASK_PARAM_JOB_DATA_KEY));
    }
}