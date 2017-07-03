package com.bee.scheduler.model;

import java.util.Date;

/**
 * @author weiwei 任务历史
 */
public class TaskHistory {
    public enum TaskExecState {
        SUCCESS, FAIL, VETOED
    }

    //触发类型：调度触发
    public static Integer TRIGGER_TYPE_SCHEDULER = 1;
    //触发类型：手动触发
    public static Integer TRIGGER_TYPE_MANUAL = 2;
    //触发类型：临时任务
    public static Integer TRIGGER_TYPE_TMP = 3;

    private String schedulerName;
    private String instanceName;
    private String fireId;
    private String taskName;
    private String taskGroup;
    private Date startTime;
    private Date completeTime;
    private Long expendTime;
    private int refired;
    private TaskExecState state;
    private Integer triggerType;
    private String log;

    public TaskHistory() {
    }

    public TaskHistory(String schedulerName, String instanceName, String fireId, String taskName, String taskGroup, Date startTime, Date completeTime, Long expendTime, int refired, TaskExecState state, Integer triggerType, String log) {
        super();
        this.schedulerName = schedulerName;
        this.instanceName = instanceName;
        this.fireId = fireId;
        this.taskName = taskName;
        this.taskGroup = taskGroup;
        this.startTime = startTime;
        this.completeTime = completeTime;
        this.expendTime = expendTime;
        this.refired = refired;
        this.state = state;
        this.triggerType = triggerType;
        this.log = log;
    }

    public String getFireId() {
        return fireId;
    }

    public void setFireId(String fireId) {
        this.fireId = fireId;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public Long getExpendTime() {
        return expendTime;
    }

    public void setExpendTime(Long expendTime) {
        this.expendTime = expendTime;
    }

    public int getRefired() {
        return refired;
    }

    public void setRefired(int refired) {
        this.refired = refired;
    }

    public TaskExecState getState() {
        return state;
    }

    public void setState(TaskExecState state) {
        this.state = state;
    }

    public Integer getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(Integer triggerType) {
        this.triggerType = triggerType;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
