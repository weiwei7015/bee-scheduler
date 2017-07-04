package com.bee.scheduler.admin.service.impl;

import com.bee.scheduler.admin.dao.SchedulerDao;
import com.bee.scheduler.admin.model.ClusterSchedulerNode;
import com.bee.scheduler.admin.service.SchedulerService;
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
