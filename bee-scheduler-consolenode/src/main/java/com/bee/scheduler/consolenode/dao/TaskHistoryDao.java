package com.bee.scheduler.consolenode.dao;

import com.bee.scheduler.consolenode.model.ExecutedTask;
import com.bee.scheduler.consolenode.model.Pageable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author weiwei
 */
@Repository
public class TaskHistoryDao extends AbstractDao {

    public ExecutedTask query(String fireId) {
        try {
            return jdbcTemplate.queryForObject("SELECT SCHED_NAME 'schedulerName',INSTANCE_ID 'instanceId',FIRE_ID 'fireId',FIRED_WAY 'firedWay',TASK_NAME 'name',TASK_GROUP 'group',EXEC_MODULE 'execModule',FIRED_TIME 'firedTime',COMPLETE_TIME 'completeTime',EXPEND_TIME 'expendTime',REFIRED 'refired',EXEC_STATE 'execState',LOG 'log' FROM BS_TASK_HISTORY WHERE FIRE_ID = ?", new BeanPropertyRowMapper<>(ExecutedTask.class), fireId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Pageable<ExecutedTask> query(String schedulerName, List<String> fireIdList, List<String> taskNameList, List<String> taskGroupList, List<String> execStateList, List<String> firedWayList, List<String> instanceIdList, Long firedTimeBefore, Long firedTimeAfter, int page) {
        StringBuilder sqlQueryResult = new StringBuilder("SELECT SCHED_NAME 'schedulerName',INSTANCE_ID 'instanceId',FIRE_ID 'fireId',TASK_NAME 'name',TASK_GROUP 'group',EXEC_MODULE 'execModule',FIRED_TIME 'firedTime',FIRED_WAY 'firedWay',COMPLETE_TIME 'completeTime',EXPEND_TIME 'expendTime',REFIRED 'refired',EXEC_STATE 'execState' FROM BS_TASK_HISTORY");
        StringBuilder sqlQueryResultCount = new StringBuilder("SELECT COUNT(1) FROM BS_TASK_HISTORY");

        List<String> conditions = new ArrayList<>();
        conditions.add(" SCHED_NAME = :schedulerName");
        if (!CollectionUtils.isEmpty(fireIdList)) {
            conditions.add("FIRE_ID in (:fireIdList)");
        }
        if (!CollectionUtils.isEmpty(taskNameList)) {
            conditions.add("TASK_NAME in (:taskNameList)");
        }
        if (!CollectionUtils.isEmpty(taskGroupList)) {
            conditions.add("TASK_GROUP in (:taskGroupList)");
        }
        if (!CollectionUtils.isEmpty(execStateList)) {
            conditions.add("EXEC_STATE in (:execStateList)");
        }
        if (!CollectionUtils.isEmpty(firedWayList)) {
            conditions.add("FIRED_WAY in (:firedWayList)");
        }
        if (!CollectionUtils.isEmpty(instanceIdList)) {
            conditions.add("INSTANCE_ID in (:instanceIdList)");
        }
        if (firedTimeBefore != null) {
            conditions.add("FIRED_TIME <= :firedTimeBefore");
        }
        if (firedTimeAfter != null) {
            conditions.add("FIRED_TIME >= :firedTimeAfter");
        }

        if (conditions.size() > 0) {
            sqlQueryResult.append(" where ").append(StringUtils.join(conditions, " and "));
            sqlQueryResultCount.append(" where ").append(StringUtils.join(conditions, " and "));
        }
        sqlQueryResult.append(" ORDER BY FIRED_TIME DESC LIMIT :limitOffset,:limitSize");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("schedulerName", schedulerName);
        paramMap.put("fireIdList", fireIdList);
        paramMap.put("taskNameList", taskNameList);
        paramMap.put("taskGroupList", taskGroupList);
        paramMap.put("execStateList", execStateList);
        paramMap.put("firedWayList", firedWayList);
        paramMap.put("instanceIdList", instanceIdList);
        paramMap.put("firedTimeBefore", firedTimeBefore);
        paramMap.put("firedTimeAfter", firedTimeAfter);
        paramMap.put("limitOffset", (page - 1) * DEFAULT_PAGE_SIZE);
        paramMap.put("limitSize", DEFAULT_PAGE_SIZE);

        List<ExecutedTask> result = namedParameterJdbcTemplate.query(sqlQueryResult.toString(), paramMap, new BeanPropertyRowMapper<>(ExecutedTask.class));
        Integer resultTotal = namedParameterJdbcTemplate.queryForObject(sqlQueryResultCount.toString(), paramMap, Integer.TYPE);
        return new Pageable<>(page, DEFAULT_PAGE_SIZE, resultTotal, result);
    }

    public List<String> getTaskHistoryGroups(String schedulerName) {
        String sql = "SELECT DISTINCT TASK_GROUP FROM BS_TASK_HISTORY WHERE SCHED_NAME = ?";
        return jdbcTemplate.queryForList(sql, String.class, schedulerName);
    }

    public int insert(final List<ExecutedTask> executedTaskList) {
        String sql = "INSERT INTO BS_TASK_HISTORY(SCHED_NAME,INSTANCE_ID,FIRE_ID, TASK_NAME, TASK_GROUP, EXEC_MODULE, FIRED_TIME, FIRED_WAY, COMPLETE_TIME, EXPEND_TIME, REFIRED, EXEC_STATE, LOG) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        int[] results = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ExecutedTask executedTask = executedTaskList.get(i);
                ps.setString(1, executedTask.getSchedulerName());
                ps.setString(2, executedTask.getInstanceId());
                ps.setString(3, executedTask.getFireId());
                ps.setString(4, executedTask.getName());
                ps.setString(5, executedTask.getGroup());
                ps.setString(6, executedTask.getExecModule());
                ps.setLong(7, executedTask.getFiredTime());
                ps.setString(8, executedTask.getFiredWay().toString());
                ps.setLong(9, executedTask.getCompleteTime());
                ps.setLong(10, executedTask.getExpendTime());
                ps.setInt(11, executedTask.getRefired());
                ps.setString(12, executedTask.getExecState().toString());
                ps.setString(13, executedTask.getLog());
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

    public Pageable<String> queryTaskNames(String schedulerName, String kw, int page, int pageSize) {
        StringBuilder sqlQueryResult = new StringBuilder("select distinct TASK_NAME from BS_TASK_HISTORY");
        StringBuilder sqlQueryResultCount = new StringBuilder("select count(distinct TASK_NAME) from BS_TASK_HISTORY");

        List<String> conditions = new ArrayList<>();
        conditions.add("SCHED_NAME = :schedulerName");
        if (StringUtils.isNotBlank(kw)) {
            conditions.add("TASK_NAME like :kw");
        }

        if (conditions.size() > 0) {
            sqlQueryResult.append(" where ").append(StringUtils.join(conditions, " and "));
            sqlQueryResultCount.append(" where ").append(StringUtils.join(conditions, " and "));
        }
        sqlQueryResult.append(" limit :limitOffset,:limitSize");


        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("schedulerName", schedulerName);
        paramMap.put("kw", kw + "%");
        paramMap.put("limitOffset", (page - 1) * pageSize);
        paramMap.put("limitSize", pageSize);

        List<String> result = namedParameterJdbcTemplate.queryForList(sqlQueryResult.toString(), paramMap, String.class);
        Integer resultTotal = namedParameterJdbcTemplate.queryForObject(sqlQueryResultCount.toString(), paramMap, Integer.TYPE);
        return new Pageable<>(page, pageSize, resultTotal, result);
    }

    public Pageable<String> queryTaskGroups(String schedulerName, String kw, int page, int pageSize) {
        StringBuilder sqlQueryResult = new StringBuilder("select distinct TASK_GROUP from BS_TASK_HISTORY");
        StringBuilder sqlQueryResultCount = new StringBuilder("select count(distinct TASK_GROUP) from BS_TASK_HISTORY");

        List<String> conditions = new ArrayList<>();
        conditions.add("SCHED_NAME = :schedulerName");
        if (StringUtils.isNotBlank(kw)) {
            conditions.add("TASK_GROUP like :kw");
        }

        if (conditions.size() > 0) {
            sqlQueryResult.append(" where ").append(StringUtils.join(conditions, " and "));
            sqlQueryResultCount.append(" where ").append(StringUtils.join(conditions, " and "));
        }
        sqlQueryResult.append(" limit :limitOffset,:limitSize");


        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("schedulerName", schedulerName);
        paramMap.put("kw", kw + "%");
        paramMap.put("limitOffset", (page - 1) * pageSize);
        paramMap.put("limitSize", pageSize);

        List<String> result = namedParameterJdbcTemplate.queryForList(sqlQueryResult.toString(), paramMap, String.class);
        Integer resultTotal = namedParameterJdbcTemplate.queryForObject(sqlQueryResultCount.toString(), paramMap, Integer.TYPE);
        return new Pageable<>(page, pageSize, resultTotal, result);
    }
}
