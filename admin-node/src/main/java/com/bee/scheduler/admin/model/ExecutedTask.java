package com.bee.scheduler.admin.model;

import com.bee.scheduler.core.Constants;

/**
 * @author weiwei 任务历史
 */
public class ExecutedTask extends FiredTask {
    private Long completeTime;
    private Long expendTime;
    private Integer refired;
    private Constants.TaskExecState execState;
    private String log;

    public ExecutedTask() {
    }

    public ExecutedTask(String schedulerName, String instanceId, String fireId, String name, String group, Long firedTime, Long completeTime, Long expendTime, int refired, Constants.TaskExecState execState, Constants.TaskFiredWay firedWay, String log) {
        this.setSchedulerName(schedulerName);
        this.setInstanceId(instanceId);
        this.setFireId(fireId);
        this.setName(name);
        this.setGroup(group);
        this.setFiredTime(firedTime);
        this.completeTime = completeTime;
        this.expendTime = expendTime;
        this.refired = refired;
        this.execState = execState;
        this.setFiredWay(firedWay);
        this.log = log;
    }

    public Long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Long completeTime) {
        this.completeTime = completeTime;
    }

    public Long getExpendTime() {
        return expendTime;
    }

    public void setExpendTime(Long expendTime) {
        this.expendTime = expendTime;
    }

    public Integer getRefired() {
        return refired;
    }

    public void setRefired(Integer refired) {
        this.refired = refired;
    }

    public Constants.TaskExecState getExecState() {
        return execState;
    }

    public void setExecState(Constants.TaskExecState execState) {
        this.execState = execState;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
