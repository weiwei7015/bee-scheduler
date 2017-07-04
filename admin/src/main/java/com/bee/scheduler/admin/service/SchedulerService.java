package com.bee.scheduler.admin.service;


import com.bee.scheduler.admin.model.ClusterSchedulerNode;

import java.util.List;

/**
 * @author weiwei
 */
public interface SchedulerService {
    List<ClusterSchedulerNode> getAllClusterScheduler(String schedulerName);
}
