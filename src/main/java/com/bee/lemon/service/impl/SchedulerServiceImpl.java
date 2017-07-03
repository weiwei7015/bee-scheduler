package com.bee.lemon.service.impl;

import com.bee.lemon.dao.SchedulerDao;
import com.bee.lemon.dao.TaskDao;
import com.bee.lemon.dao.TaskHistoryDao;
import com.bee.lemon.model.ClusterSchedulerNode;
import com.bee.lemon.model.Pageable;
import com.bee.lemon.model.Task;
import com.bee.lemon.model.TaskHistory;
import com.bee.lemon.service.SchedulerService;
import com.bee.lemon.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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
