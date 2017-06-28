package com.bee.lemon.dao;

import com.bee.lemon.model.Pageable;
import com.bee.lemon.model.TaskHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author weiwei1
 */
@Repository
public class TaskHistoryDao extends DaoBase {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insert(final List<TaskHistory> taskHistoryList) {
        String sql = "INSERT INTO TASK_HISTORY(FIRE_ID, TASK_NAME, TASK_GROUP, START_TIME, COMPLETE_TIME, EXPENDTIME, REFIRED, STATE, TRIGGER_TYPE, LOG) VALUES (?,?,?,?,?,?,?,?,?,?)";
        int[] results = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                TaskHistory history = taskHistoryList.get(i);
                ps.setString(1, history.getFireId());
                ps.setString(2, history.getTaskName());
                ps.setString(3, history.getTaskGroup());
                ps.setTimestamp(4, new Timestamp(history.getStartTime().getTime()));
                ps.setTimestamp(5, new Timestamp(history.getCompleteTime().getTime()));
                ps.setLong(6, history.getExpendTime());
                ps.setInt(7, history.getRefired());
                ps.setString(8, history.getState().toString());
                ps.setInt(9, history.getTriggerType());
                ps.setString(10, history.getLog());
            }

            @Override
            public int getBatchSize() {
                return taskHistoryList.size();
            }
        });
        int tmp = 0;
        for (int result : results) {
            tmp += result;
        }
        return tmp;
    }

    public int clearBefore(Date date) {
        String sql = "DELETE FROM TASK_HISTORY WHERE START_TIME <= '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "'");
        return jdbcTemplate.update(sql);
    }

    public Pageable<TaskHistory> query(String fireId, String taskName, String taskGroup, String state, Integer triggerType, Long beginTime, Long endTime, int page) {
        List<Object> args = new ArrayList<>();
        StringBuffer sqlQueryResultCount = new StringBuffer("SELECT COUNT(1) FROM TASK_HISTORY");
        StringBuffer sqlQueryResult = new StringBuffer("SELECT LIMIT ? ? * FROM TASK_HISTORY");

        StringBuffer sqlWhere = new StringBuffer(" WHERE 1=1");
        if (fireId != null) {
            sqlWhere.append(" AND FIRE_ID = ?");
            args.add(fireId);
        }
        if (taskName != null) {
            sqlWhere.append(" AND TASK_NAME LIKE ?");
            args.add("%" + taskName + "%");
        }
        if (taskGroup != null) {
            sqlWhere.append(" AND TASK_GROUP LIKE ?");
            args.add("%" + taskGroup + "%");
        }
        if (state != null) {
            sqlWhere.append(" AND STATE = ?");
            args.add(state);
        }
        if (triggerType != null) {
            sqlWhere.append(" AND TRIGGER_TYPE = ?");
            args.add(triggerType);
        }
        if (beginTime != null) {
            sqlWhere.append(" AND START_TIME >= ?");
            args.add(beginTime);
        }
        if (endTime != null) {
            sqlWhere.append(" AND START_TIME <= ?");
            args.add(endTime);
        }
        // 查询记录总数
        Integer resultTotal = jdbcTemplate.queryForObject(sqlQueryResultCount.append(sqlWhere).toString(), Integer.class, args.toArray());
        // 查询记录
        // sqlQueryResult.append(sqlWhere).append(" ORDER BY START_TIME DESC LIMIT ?,?");

        args.add(0, (page - 1) * getPageSize());
        args.add(1, getPageSize());
        sqlQueryResult.append(sqlWhere).append(" ORDER BY START_TIME DESC");
        List<TaskHistory> result = jdbcTemplate.query(sqlQueryResult.toString(), new BeanPropertyRowMapper<TaskHistory>(TaskHistory.class), args.toArray());

        return new Pageable<>(page, getPageSize(), resultTotal, result);
    }

    public TaskHistory query(String fireId) {
        String sql = "SELECT * FROM TASK_HISTORY WHERE FIRE_ID = ?";
        TaskHistory history = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<TaskHistory>(TaskHistory.class), fireId);
        return history;
    }

    public List<String> getTaskHistoryGroups() {
        List<String> result = new ArrayList<>();
        String sql = "SELECT DISTINCT TASK_GROUP FROM TASK_HISTORY";
        result = jdbcTemplate.queryForList(sql, String.class);
        return result;
    }

}
