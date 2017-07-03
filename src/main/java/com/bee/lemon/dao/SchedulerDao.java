package com.bee.lemon.dao;

import com.bee.lemon.model.ClusterSchedulerNode;
import com.bee.lemon.model.TaskHistory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author weiwei
 */
@Repository
public class SchedulerDao extends DaoBase {
    public List<ClusterSchedulerNode> getAllClusterScheduler(String schedulerName) {
        StringBuilder sqlQueryResult = new StringBuilder("select t.SCHED_NAME 'name',t.INSTANCE_NAME 'instanceName',t.LAST_CHECKIN_TIME 'lastCheckin',t.CHECKIN_INTERVAL 'checkinInterval' from qrtz_scheduler_state t where t.SCHED_NAME = ?");

        List<ClusterSchedulerNode> result = new ArrayList<>();

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