package com.bee.scheduler.service.impl;

import com.bee.scheduler.dao.SchedulerDao;
import com.bee.scheduler.model.ClusterSchedulerNode;
import com.bee.scheduler.service.SchedulerService;
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
