package com.bee.scheduler.consolenode.dao;

import com.bee.scheduler.consolenode.entity.User;
import com.bee.scheduler.consolenode.model.ClusterSchedulerNode;
import com.bee.scheduler.consolenode.model.ExecutedTask;
import com.bee.scheduler.consolenode.model.Pageable;
import com.bee.scheduler.consolenode.model.TaskDetail;
import com.bee.scheduler.context.common.Constants;
import com.bee.scheduler.context.common.TaskSpecialGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class DaoSupport extends AbstractDao {
    /**
     * ================= User =====================
     */
    public User getUserByAccount$Pwd(String account, String pwd) {
        try {
            return jdbcTemplate.queryForObject("select * from BS_USER where ACCOUNT = ? and PWD = ?", new BeanPropertyRowMapper<>(User.class), account, pwd);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updateUserByAccount(User entity) {
        List<String> setters = new ArrayList<>();
        if (entity.getPwd() != null) {
            setters.add("PWD = :pwd");
        }
        if (entity.getName() != null) {
            setters.add("NAME = :name");
        }
        String sql = "update BS_USER set " + String.join(",", setters) + " where ACCOUNT = :account";
        namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(entity));
    }

    /**
     * ================= Cluster =====================
     */
    public List<ClusterSchedulerNode> getClusterSchedulerNodes(String schedulerName) {
        return jdbcTemplate.query("select SCHED_NAME as 'name',INSTANCE_NAME as 'instanceName',LAST_CHECKIN_TIME as 'lastCheckinTime',CHECKIN_INTERVAL as 'checkinInterval' from BS_SCHEDULER_STATE t where SCHED_NAME = ?", new RowMapper<ClusterSchedulerNode>() {
            @Override
            public ClusterSchedulerNode mapRow(ResultSet rs, int rowNum) throws SQLException {
                ClusterSchedulerNode clusterSchedulerNode = new ClusterSchedulerNode();
                clusterSchedulerNode.setName(rs.getString("name"));
                clusterSchedulerNode.setInstanceName(rs.getString("instanceName"));
                clusterSchedulerNode.setLastCheckinTime(new Date(rs.getLong("lastCheckinTime")));
                clusterSchedulerNode.setCheckinInterval(rs.getLong("checkinInterval"));
                return clusterSchedulerNode;
            }
        }, schedulerName);
    }

    /**
     * ================= Task =====================
     */
    public Pageable<TaskDetail> queryTask(String schedulerName, List<String> taskNameList, List<String> taskGroupList, List<String> taskStateList, Integer pageNum, Integer pageSize) {
        StringBuilder sqlQueryCount = new StringBuilder("SELECT COUNT(1) FROM BS_TRIGGERS t1 JOIN BS_JOB_DETAILS t2 ON t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");
        StringBuilder sqlQueryResult = new StringBuilder("SELECT t1.SCHED_NAME as 'schedulerName',t1.JOB_NAME as 'name',t1.JOB_GROUP as 'group',t1.TRIGGER_TYPE as 'triggerType',t2.JOB_CLASS_NAME as 'jobClassName',t1.JOB_DATA as 'data',t1.TRIGGER_STATE as 'state',t1.PREV_FIRE_TIME as 'prevFireTime',t1.NEXT_FIRE_TIME as 'nextFireTime',t1.START_TIME as 'startTime',t1.END_TIME as 'endTime',t1.MISFIRE_INSTR as 'misfireInstr',t1.DESCRIPTION as 'description' FROM BS_TRIGGERS t1 JOIN BS_JOB_DETAILS t2 ON t1.SCHED_NAME = t2.SCHED_NAME AND t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");

        List<String> conditions = new ArrayList<>();
        conditions.add("t1.SCHED_NAME = :schedulerName");
        conditions.add("t1.TRIGGER_GROUP NOT IN(:taskSpecialGroup)");
        if (!CollectionUtils.isEmpty(taskNameList)) {
            conditions.add("t1.JOB_NAME in (:taskNameList)");
        }
        if (!CollectionUtils.isEmpty(taskGroupList)) {
            conditions.add("t1.JOB_GROUP in (:taskGroupList)");
        }
        if (!CollectionUtils.isEmpty(taskStateList)) {
            conditions.add("t1.TRIGGER_STATE in (:taskStateList)");
        }
        if (conditions.size() > 0) {
            sqlQueryResult.append(" where ").append(StringUtils.join(conditions, " and "));
            sqlQueryCount.append(" where ").append(StringUtils.join(conditions, " and "));
        }
        sqlQueryResult.append(" ORDER BY t1.JOB_GROUP,t1.JOB_NAME ASC LIMIT :limitOffset,:limitSize");


        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("schedulerName", schedulerName);
        paramMap.put("taskSpecialGroup", TaskSpecialGroup.stringValues());
        paramMap.put("taskNameList", taskNameList);
        paramMap.put("taskGroupList", taskGroupList);
        paramMap.put("taskStateList", taskStateList);
        paramMap.put("limitOffset", (pageNum - 1) * pageSize);
        paramMap.put("limitSize", pageSize);

        Integer resultTotal = namedParameterJdbcTemplate.queryForObject(sqlQueryCount.toString(), paramMap, Integer.TYPE);
        List<TaskDetail> result = new ArrayList<>();
        namedParameterJdbcTemplate.query(sqlQueryResult.toString(), paramMap, rs -> {
            TaskDetail task = new TaskDetail();
            task.setSchedulerName(rs.getString("schedulerName"));
            task.setName(rs.getString("name"));
            task.setGroup(rs.getString("group"));
            task.setTriggerType(rs.getString("triggerType"));
            Properties data = getPropsFromBlob(rs, "data");
            String execModule = data == null ? null : String.valueOf(data.get(Constants.TRIGGER_DATA_KEY_TASK_MODULE_ID));
            task.setExecModule(execModule);
            task.setPrevFireTime(rs.getLong("prevFireTime"));
            task.setNextFireTime(rs.getLong("nextFireTime"));
            task.setStartTime(rs.getLong("startTime"));
            task.setEndTime(rs.getLong("endTime"));
            task.setMisfireInstr(rs.getInt("misfireInstr"));
            task.setState(rs.getString("state"));
            task.setDescription(rs.getString("description"));
            task.setData(data);
            result.add(task);
        });
        return new Pageable<>(pageNum, pageSize, resultTotal, result);
    }

    public Pageable<String> queryTaskGroups(String schedulerName, String kw, Integer page, Integer pageSize) {
        StringBuilder sqlQueryResult = new StringBuilder("SELECT distinct t1.JOB_GROUP FROM BS_TRIGGERS t1 JOIN BS_JOB_DETAILS t2 ON t1.SCHED_NAME = t2.SCHED_NAME AND t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");
        StringBuilder sqlQueryResultCount = new StringBuilder("SELECT COUNT(distinct t1.JOB_GROUP) FROM BS_TRIGGERS t1 JOIN BS_JOB_DETAILS t2 ON t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");

        List<String> conditions = new ArrayList<>();
        conditions.add("t1.SCHED_NAME = :schedulerName");
        if (StringUtils.isNotBlank(kw)) {
            conditions.add("t1.JOB_GROUP like :kw");
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

    public Pageable<String> queryTaskNames(String schedulerName, String kw, Integer page, Integer pageSize) {
        StringBuilder sqlQueryResult = new StringBuilder("SELECT distinct t1.JOB_NAME FROM BS_TRIGGERS t1 JOIN BS_JOB_DETAILS t2 ON t1.SCHED_NAME = t2.SCHED_NAME AND t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");
        StringBuilder sqlQueryResultCount = new StringBuilder("SELECT COUNT(distinct t1.JOB_NAME) FROM BS_TRIGGERS t1 JOIN BS_JOB_DETAILS t2 ON t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");

        List<String> conditions = new ArrayList<>();
        conditions.add("t1.SCHED_NAME = :schedulerName");
        if (StringUtils.isNotBlank(kw)) {
            conditions.add("t1.JOB_NAME like :kw");
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

    /**
     * ================= TaskHistory =====================
     */
    public ExecutedTask getTaskHistory(String fireId) {
        try {
            return jdbcTemplate.queryForObject("SELECT SCHED_NAME as 'schedulerName',INSTANCE_ID as 'instanceId',FIRE_ID as 'fireId',FIRED_WAY as 'firedWay',TASK_NAME as 'name',TASK_GROUP as 'group',EXEC_MODULE as 'execModule',FIRED_TIME as 'firedTime',COMPLETE_TIME as 'completeTime',EXPEND_TIME as 'expendTime',REFIRED as 'refired',EXEC_STATE as 'execState',LOG as 'log' FROM BS_TASK_HISTORY WHERE FIRE_ID = ?", new BeanPropertyRowMapper<>(ExecutedTask.class), fireId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Pageable<ExecutedTask> queryTaskHistory(String schedulerName, List<String> fireIdList, List<String> taskNameList, List<String> taskGroupList, List<String> execStateList, List<String> firedWayList, List<String> instanceIdList, Long firedTimeBefore, Long firedTimeAfter, Integer pageNum, Integer pageSize) {
        StringBuilder sqlQueryResult = new StringBuilder("SELECT SCHED_NAME as 'schedulerName',INSTANCE_ID as 'instanceId',FIRE_ID as 'fireId',TASK_NAME as 'name',TASK_GROUP as 'group',EXEC_MODULE as 'execModule',FIRED_TIME as 'firedTime',FIRED_WAY as 'firedWay',COMPLETE_TIME as 'completeTime',EXPEND_TIME as 'expendTime',REFIRED as 'refired',EXEC_STATE as 'execState' FROM BS_TASK_HISTORY");
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
        paramMap.put("limitOffset", (pageNum - 1) * pageSize);
        paramMap.put("limitSize", pageSize);

        List<ExecutedTask> result = namedParameterJdbcTemplate.query(sqlQueryResult.toString(), paramMap, new BeanPropertyRowMapper<>(ExecutedTask.class));
        Integer resultTotal = namedParameterJdbcTemplate.queryForObject(sqlQueryResultCount.toString(), paramMap, Integer.TYPE);
        return new Pageable<>(pageNum, pageSize, resultTotal, result);
    }

    public Pageable<String> queryTaskHistoryGroups(String schedulerName, String kw, Integer pageNum, Integer pageSize) {
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
        paramMap.put("limitOffset", (pageNum - 1) * pageSize);
        paramMap.put("limitSize", pageSize);

        List<String> result = namedParameterJdbcTemplate.queryForList(sqlQueryResult.toString(), paramMap, String.class);
        Integer resultTotal = namedParameterJdbcTemplate.queryForObject(sqlQueryResultCount.toString(), paramMap, Integer.TYPE);
        return new Pageable<>(pageNum, pageSize, resultTotal, result);
    }

    public Pageable<String> queryTaskHistoryNames(String schedulerName, String kw, Integer pageNum, Integer pageSize) {
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
        paramMap.put("limitOffset", (pageNum - 1) * pageSize);
        paramMap.put("limitSize", pageSize);

        List<String> result = namedParameterJdbcTemplate.queryForList(sqlQueryResult.toString(), paramMap, String.class);
        Integer resultTotal = namedParameterJdbcTemplate.queryForObject(sqlQueryResultCount.toString(), paramMap, Integer.TYPE);
        return new Pageable<>(pageNum, pageSize, resultTotal, result);
    }

    public Properties getPropsFromBlob(ResultSet rs, String colName) {
        try (InputStream binaryInput = rs.getBinaryStream(colName)) {
            if (binaryInput != null) {
                Properties properties = new Properties();
                properties.load(binaryInput);
                return properties;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}