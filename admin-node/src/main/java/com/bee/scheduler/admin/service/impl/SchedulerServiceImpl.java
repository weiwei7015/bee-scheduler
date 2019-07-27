package com.bee.scheduler.daemonnode.service.impl;

import com.bee.scheduler.daemonnode.dao.SchedulerDao;
import com.bee.scheduler.daemonnode.model.ClusterSchedulerNode;
import com.bee.scheduler.daemonnode.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private SchedulerDao schedulerDao;

    @Override
    public List<ClusterSchedulerNode> getAllClusterScheduler(String schedulerName) {
        return schedulerDao.getAllClusterScheduler(schedulerName);
    }
}
