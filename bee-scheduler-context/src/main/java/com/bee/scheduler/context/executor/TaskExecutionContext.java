package com.bee.scheduler.context.executor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.ExecutionContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

/**
 * @author weiwei
 */
public class TaskExecutionContext implements ExecutionContext {
    private final Log logger = LogFactory.getLog("TaskLogger");
    private final JobExecutionContext jobExecutionContext;
    private final String executorModuleId;
    private final JSONObject param;
    private final JSONArray linkageRule;

    public TaskExecutionContext(JobExecutionContext jobExecutionContext, String executorModuleId, JSONObject param, JSONArray linkageRule) {
        this.jobExecutionContext = jobExecutionContext;
        this.executorModuleId = executorModuleId;
        this.param = param;
        this.linkageRule = linkageRule;
    }

    @Override
    public JSONObject getParam() {
        return param;
    }

    @Override
    public Log getLogger() {
        return logger;
    }

    public JobExecutionContext getJobExecutionContext() {
        return jobExecutionContext;
    }

    public String getExecutorModuleId() {
        return executorModuleId;
    }

    public JSONArray getLinkageRule() {
        return linkageRule;
    }
}
