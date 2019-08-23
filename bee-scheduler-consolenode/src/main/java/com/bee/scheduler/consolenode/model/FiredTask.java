package com.bee.scheduler.consolenode.model;

import com.bee.scheduler.context.common.TaskFiredWay;

/**
 * Created by wei-wei
 */
public class FiredTask extends TaskDetail {
    private String instanceId;
    private String fireId;
    private Long firedTime;
    private TaskFiredWay firedWay;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getFireId() {
        return fireId;
    }

    public void setFireId(String fireId) {
        this.fireId = fireId;
    }

    public Long getFiredTime() {
        return firedTime;
    }

    public void setFiredTime(Long firedTime) {
        this.firedTime = firedTime;
    }

    public TaskFiredWay getFiredWay() {
        return firedWay;
    }

    public void setFiredWay(TaskFiredWay firedWay) {
        this.firedWay = firedWay;
    }
}
