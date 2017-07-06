package com.bee.scheduler.admin.model;

import java.util.Map;

/**
 * @author weiwei
 */
public class Task {
    private String schedulerName;
    private String name;
    private String group;
    private String triggerType;
    private String jobComponent;
    private Integer priority;
    private Map<Object, Object> data;
    private String state;
    private Long nextFireTime;
    private Long prevFireTime;
    private Long startTime;
    private Long endTime;
    private Integer misfireInstr;
    private String description;

    public Task() {

    }

    public Task(String schedulerName, String name, String group, String jobComponent) {
        this.schedulerName = schedulerName;
        this.name = name;
        this.group = group;
        this.jobComponent = jobComponent;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getJobComponent() {
        return jobComponent;
    }

    public void setJobComponent(String jobComponent) {
        this.jobComponent = jobComponent;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Map<Object, Object> getData() {
        return data;
    }

    public void setData(Map<Object, Object> data) {
        this.data = data;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Long nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Long getPrevFireTime() {
        return prevFireTime;
    }

    public void setPrevFireTime(Long prevFireTime) {
        this.prevFireTime = prevFireTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getMisfireInstr() {
        return misfireInstr;
    }

    public void setMisfireInstr(Integer misfireInstr) {
        this.misfireInstr = misfireInstr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
