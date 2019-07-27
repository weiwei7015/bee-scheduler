package com.bee.scheduler.consolenode.service;


import com.bee.scheduler.consolenode.model.ClusterSchedulerNode;

import java.util.List;

/**
 * @author weiwei
 */
public interface SchedulerService {
    List<ClusterSchedulerNode> getAllClusterScheduler(String schedulerName);
}
