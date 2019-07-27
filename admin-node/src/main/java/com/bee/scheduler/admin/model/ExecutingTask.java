package com.bee.scheduler.daemonnode.model;

/**
 * Created by wei-wei
 */
public class ExecutingTask extends FiredTask {
    private Boolean isNonConcurrent;
    private Boolean requestsRecovery;

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
