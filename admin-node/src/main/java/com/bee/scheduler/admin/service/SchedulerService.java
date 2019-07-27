package com.bee.scheduler.daemonnode.service;


import com.bee.scheduler.daemonnode.model.ClusterSchedulerNode;

import java.util.List;

/**
 * @author weiwei
 */
public interface SchedulerService {
    List<ClusterSchedulerNode> getAllClusterScheduler(String schedulerName);
}
