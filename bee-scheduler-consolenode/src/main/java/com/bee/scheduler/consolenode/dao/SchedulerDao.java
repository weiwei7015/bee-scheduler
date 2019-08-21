package com.bee.scheduler.consolenode.dao;

import com.bee.scheduler.consolenode.model.ClusterSchedulerNode;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author weiwei
 */
@Repository
public class SchedulerDao extends AbstractDao {
    public List<ClusterSchedulerNode> getAllClusterScheduler(String schedulerName) {
        return jdbcTemplate.query("select SCHED_NAME 'name',INSTANCE_NAME 'instanceName',LAST_CHECKIN_TIME 'lastCheckinTime',CHECKIN_INTERVAL 'checkinInterval' from BS_SCHEDULER_STATE t where SCHED_NAME = ?", new BeanPropertyRowMapper<>(ClusterSchedulerNode.class), schedulerName);
    }
}