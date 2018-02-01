package com.bee.scheduler.admin.dao;

import com.bee.scheduler.admin.model.ExecutedTask;
import com.bee.scheduler.admin.model.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author weiwei
 */
@Repository
public class TaskHistoryDao extends DaoBase {

    public ExecutedTask query(String fireId) {
        String sql = "SELECT t.SCHED_NAME 'schedulerName',t.INSTANCE_ID 'instanceId',t.FIRE_ID 'fireId',t.FIRED_WAY 'firedWay',t.TASK_NAME 'name',t.TASK_GROUP 'group',t.FIRED_TIME 'firedTime',t.COMPLETE_TIME 'completeTime',t.EXPEND_TIME 'expendTime',t.REFIRED 'refired',t.EXEC_STATE 'execState',t.LOG 'log' FROM BS_TASK_HISTORY t WHERE t.FIRE_ID = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(ExecutedTask.class), fireId);
    }

    public Pageable<ExecutedTask> query(String schedulerName, String fireId, String taskName, String taskGroup, String execState, String firedWay, String instanceId, Long starTimeFrom, Long startTimeTo, int page) {
        List<Object> args = new ArrayList<>();
        StringBuilder sqlQueryResultCount = new StringBuilder("SELECT COUNT(1) FROM BS_TASK_HISTORY");
//        StringBuilder sqlQueryResult = new StringBuilder("SELECT t.SCHED_NAME 'schedulerName',t.INSTANCE_ID 'instanceId',t.FIRE_ID 'fireId',t.TASK_NAME 'name',t.TASK_GROUP 'group',t.FIRED_TIME 'firedTime',t.FIRED_WAY 'firedWay',t.COMPLETE_TIME 'completeTime',t.EXPEND_TIME 'expendTime',t.REFIRED 'refired',t.EXEC_STATE 'execState',t.LOG 'log' FROM BS_TASK_HISTORY t");
        StringBuilder sqlQueryResult = new StringBuilder("SELECT t.SCHED_NAME 'schedulerName',t.INSTANCE_ID 'instanceId',t.FIRE_ID 'fireId',t.TASK_NAME 'name',t.TASK_GROUP 'group',t.FIRED_TIME 'firedTime',t.FIRED_WAY 'firedWay',t.COMPLETE_TIME 'completeTime',t.EXPEND_TIME 'expendTime',t.REFIRED 'refired',t.EXEC_STATE 'execState' FROM BS_TASK_HISTORY t");

        StringBuilder sqlWhere = new StringBuilder(" WHERE SCHED_NAME = ?");
        args.add(schedulerName);
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
        if (execState != null) {
            sqlWhere.append(" AND EXEC_STATE = ?");
            args.add(execState);
        }
        if (firedWay != null) {
            sqlWhere.append(" AND FIRED_WAY = ?");
            args.add(firedWay);
        }
        if (instanceId != null) {
            sqlWhere.append(" AND INSTANCE_ID = ?");
            args.add(instanceId);
        }
        if (starTimeFrom != null) {
            sqlWhere.append(" AND FIRED_TIME >= ?");
            args.add(starTimeFrom);
        }
        if (startTimeTo != null) {
            sqlWhere.append(" AND FIRED_TIME <= ?");
            args.add(startTimeTo);
        }
        // 查询记录总数
        Integer resultTotal = jdbcTemplate.queryForObject(sqlQueryResultCount.append(sqlWhere).toString(), Integer.class, args.toArray());
        // 查询记录
        sqlQueryResult.append(sqlWhere).append(" ORDER BY FIRED_TIME DESC LIMIT ?,?");

        args.add((page - 1) * pageSize);
        args.add(pageSize);
        List<ExecutedTask> result = jdbcTemplate.query(sqlQueryResult.toString(), new BeanPropertyRowMapper<>(ExecutedTask.class), args.toArray());

        return new Pageable<>(page, pageSize, resultTotal, result);
    }

    public List<String> getTaskHistoryGroups(String schedulerName) {
        String sql = "SELECT DISTINCT TASK_GROUP FROM BS_TASK_HISTORY WHERE SCHED_NAME = ?";
        return jdbcTemplate.queryForList(sql, String.class, schedulerName);
    }

    public int insert(final List<ExecutedTask> executedTaskList) {
        String sql = "INSERT INTO BS_TASK_HISTORY(SCHED_NAME,INSTANCE_ID,FIRE_ID, TASK_NAME, TASK_GROUP, FIRED_TIME, FIRED_WAY, COMPLETE_TIME, EXPEND_TIME, REFIRED, EXEC_STATE, LOG) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        int[] results = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ExecutedTask executedTask = executedTaskList.get(i);
                ps.setString(1, executedTask.getSchedulerName());
                ps.setString(2, executedTask.getInstanceId());
                ps.setString(3, executedTask.getFireId());
                ps.setString(4, executedTask.getName());
                ps.setString(5, executedTask.getGroup());
                ps.setLong(6, executedTask.getFiredTime());
                ps.setString(7, executedTask.getFiredWay().toString());
                ps.setLong(8, executedTask.getCompleteTime());
                ps.setLong(9, executedTask.getExpendTime());
                ps.setInt(10, executedTask.getRefired());
                ps.setString(11, executedTask.getExecState().toString());
                ps.setString(12, executedTask.getLog());
            }

            @Override
            public int getBatchSize() {
                return executedTaskList.size();
            }
        });
        int tmp = 0;
        for (int result : results) {
            tmp += result;
        }
        return tmp;
    }

    public int clearBefore(String schedulerName, Date date) {
        String sql = "DELETE FROM BS_TASK_HISTORY WHERE FIRED_TIME <= '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "'");
        return jdbcTemplate.update(sql);
    }

}
