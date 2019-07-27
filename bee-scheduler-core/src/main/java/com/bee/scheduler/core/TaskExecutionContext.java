package com.bee.scheduler.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * @author weiwei
 */
public class TaskExecutionContext {
    private String taskModuleId;
    private JSONObject param;
    private JSONArray linkageRule;
    private String schedulerName;
    private String schedulerInstanceId;
    private String jobGroup;
    private String jobName;
    private String triggerGroup;
    private String triggerName;
    private Date fireTime;
    private String fireInstanceId;
    private long jobRunTime;
    private int refireCount;
    private Date previousFireTime;
    private TaskExecutionLogger logger;

    public String getTaskModuleId() {
        return taskModuleId;
    }

    public void setTaskModuleId(String taskModuleId) {
        this.taskModuleId = taskModuleId;
    }

    public JSONObject getParam() {
        return param;
    }

    public void setParam(JSONObject param) {
        this.param = param;
    }

    public JSONArray getLinkageRule() {
        return linkageRule;
    }

    public void setLinkageRule(JSONArray linkageRule) {
        this.linkageRule = linkageRule;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public String getSchedulerInstanceId() {
        return schedulerInstanceId;
    }

    public void setSchedulerInstanceId(String schedulerInstanceId) {
        this.schedulerInstanceId = schedulerInstanceId;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public Date getFireTime() {
        return fireTime;
    }

    public void setFireTime(Date fireTime) {
        this.fireTime = fireTime;
    }

    public String getFireInstanceId() {
        return fireInstanceId;
    }

    public void setFireInstanceId(String fireInstanceId) {
        this.fireInstanceId = fireInstanceId;
    }

    public long getJobRunTime() {
        return jobRunTime;
    }

    public void setJobRunTime(long jobRunTime) {
        this.jobRunTime = jobRunTime;
    }

    public int getRefireCount() {
        return refireCount;
    }

    public void setRefireCount(int refireCount) {
        this.refireCount = refireCount;
    }

    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    public TaskExecutionLogger getLogger() {
        return logger;
    }

    public void setLogger(TaskExecutionLogger logger) {
        this.logger = logger;
    }
}
