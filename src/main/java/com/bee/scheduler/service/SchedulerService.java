package com.bee.scheduler.service;


import com.bee.scheduler.model.ClusterSchedulerNode;

import java.util.List;

/**
 * @author weiwei
 */
public interface SchedulerService {
    List<ClusterSchedulerNode> getAllClusterScheduler(String schedulerName);
}
