package com.bee.scheduler.admin.model;

/**
 * Created by wei-wei on 2017/7/5.
 */
public class ExecutingTask extends Task {
    private Long fireTime;
    private Boolean isNonConcurrent;
    private Boolean requestsRecovery;

    public Long getFireTime() {
        return fireTime;
    }

    public void setFireTime(Long fireTime) {
        this.fireTime = fireTime;
    }

    public Boolean getNonConcurrent() {
        return isNonConcurrent;
    }

    public void setNonConcurrent(Boolean nonConcurrent) {
        isNonConcurrent = nonConcurrent;
    }

    public Boolean getRequestsRecovery() {
        return requestsRecovery;
    }

    public void setRequestsRecovery(Boolean requestsRecovery) {
        this.requestsRecovery = requestsRecovery;
    }
}
