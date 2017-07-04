package com.bee.scheduler.admin.model;

import java.util.Date;

/**
 * Created by wei-wei
 */
public class ClusterSchedulerNode extends SchedulerNode {
    private Date lastCheckinTime;
    private Long checkinInterval;

    public Date getLastCheckinTime() {
        return lastCheckinTime;
    }

    public void setLastCheckinTime(Date lastCheckinTime) {
        this.lastCheckinTime = lastCheckinTime;
    }

    public Long getCheckinInterval() {
        return checkinInterval;
    }

    public void setCheckinInterval(Long checkinInterval) {
        this.checkinInterval = checkinInterval;
    }
}
