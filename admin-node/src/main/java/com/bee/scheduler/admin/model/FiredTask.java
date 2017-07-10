package com.bee.scheduler.admin.model;

import com.bee.scheduler.core.Constants;

/**
 * Created by wei-wei
 */
public class FiredTask extends Task {
    private String instanceId;
    private String fireId;
    private Long firedTime;
    private Constants.TaskFiredWay firedWay;

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

    public Constants.TaskFiredWay getFiredWay() {
        return firedWay;
    }

    public void setFiredWay(Constants.TaskFiredWay firedWay) {
        this.firedWay = firedWay;
    }
}
