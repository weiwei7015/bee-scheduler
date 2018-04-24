package com.bee.scheduler.admin.dao;

import com.bee.scheduler.admin.model.ClusterSchedulerNode;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author weiwei
 */
@Repository
public class SchedulerDao extends DaoBase {
    public List<ClusterSchedulerNode> getAllClusterScheduler(String schedulerName) {
        StringBuilder sqlQueryResult = new StringBuilder("select t.SCHED_NAME 'name',t.INSTANCE_NAME 'instanceName',t.LAST_CHECKIN_TIME 'lastCheckin',t.CHECKIN_INTERVAL 'checkinInterval' from BS_SCHEDULER_STATE t where t.SCHED_NAME = ?");

        final List<ClusterSchedulerNode> result = new ArrayList<>();

        jdbcTemplate.query(sqlQueryResult.toString(), rs -> {
            ClusterSchedulerNode clusterSchedulerNode = new ClusterSchedulerNode();
            clusterSchedulerNode.setName(rs.getString("name"));
            clusterSchedulerNode.setInstanceName(rs.getString("instanceName"));
            clusterSchedulerNode.setLastCheckinTime(new Date(rs.getLong("lastCheckin")));
            clusterSchedulerNode.setCheckinInterval(rs.getLong("checkinInterval"));
            result.add(clusterSchedulerNode);
        }, schedulerName);

        return result;
    }
}