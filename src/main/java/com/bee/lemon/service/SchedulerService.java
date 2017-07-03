package com.bee.lemon.service;


import com.bee.lemon.model.ClusterSchedulerNode;
import com.bee.lemon.model.Pageable;
import com.bee.lemon.model.Task;
import com.bee.lemon.model.TaskHistory;

import java.util.Date;
import java.util.List;

/**
 * @author weiwei
 */
public interface SchedulerService {
    List<ClusterSchedulerNode> getAllClusterScheduler(String schedulerName);
}
