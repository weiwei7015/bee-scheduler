package com.bee.scheduler.consolenode.dao;

import com.bee.scheduler.consolenode.model.ExecutingTask;
import com.bee.scheduler.consolenode.model.Pageable;
import com.bee.scheduler.consolenode.model.Task;
import com.bee.scheduler.context.common.Constants;
import com.bee.scheduler.context.common.TaskFiredWay;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.*;

/**
 * @author weiwei
 */
@Repository
public class TaskDao extends AbstractDao {

    public Task get(String schedulerName, String name, String group) {
        String sql = "SELECT t1.SCHED_NAME 'schedulerName',t1.JOB_NAME 'name',t1.JOB_GROUP 'group',t2.JOB_CLASS_NAME 'jobClassName',t1.JOB_DATA 'data',t1.DESCRIPTION 'description' FROM BS_TRIGGERS t1 JOIN BS_JOB_DETAILS t2 ON t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP where t1.SCHED_NAME = ? and t1.TRIGGER_NAME = ? and t1.TRIGGER_GROUP = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Task.class), schedulerName, name, group);
    }

    public Pageable<Task> query(String schedulerName, String name, String group, String state, int page) {
        List<Object> args = new ArrayList<>();
        StringBuilder sqlQueryResultCount = new StringBuilder("SELECT COUNT(1) FROM BS_TRIGGERS t1 JOIN BS_JOB_DETAILS t2 ON t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");
        StringBuilder sqlQueryResult = new StringBuilder("SELECT t1.SCHED_NAME 'schedulerName',t1.JOB_NAME 'name',t1.JOB_GROUP 'group',t1.TRIGGER_TYPE 'triggerType',t2.JOB_CLASS_NAME 'jobClassName',t1.JOB_DATA 'data',t1.TRIGGER_STATE 'state',t1.PREV_FIRE_TIME 'prevFireTime',t1.NEXT_FIRE_TIME 'nextFireTime',t1.START_TIME 'startTime',t1.END_TIME 'endTime',t1.MISFIRE_INSTR 'misfireInstr',t1.DESCRIPTION 'description' FROM BS_TRIGGERS t1 JOIN BS_JOB_DETAILS t2 ON t1.SCHED_NAME = t2.SCHED_NAME AND t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");

        StringBuilder sqlWhere = new StringBuilder(" WHERE t1.SCHED_NAME = ? AND t1.TRIGGER_GROUP NOT IN('TMP','LINKAGE')");
        args.add(schedulerName);
        if (name != null) {
            sqlWhere.append(" AND t1.JOB_NAME LIKE ?");
            args.add("%" + name + "%");
        }
        if (group != null) {
            sqlWhere.append(" AND t1.JOB_GROUP = ?");
            args.add(group);
        }
        if (state != null) {
            sqlWhere.append(" AND t1.TRIGGER_STATE = ?");
            args.add(state);
        }
        // 查询记录总数
        Integer resultTotal = jdbcTemplate.queryForObject(sqlQueryResultCount.append(sqlWhere).toString(), Integer.class, args.toArray());
        // 查询记录
        sqlQueryResult.append(sqlWhere).append(" ORDER BY t1.JOB_GROUP,t1.JOB_NAME ASC LIMIT ?,?");
        args.add((page - 1) * DEFAULT_PAGE_SIZE);
        args.add(DEFAULT_PAGE_SIZE);

        final List<Task> result = new ArrayList<>();


        jdbcTemplate.query(sqlQueryResult.toString(), rs -> {
            Task task = new Task();
            task.setSchedulerName(rs.getString("schedulerName"));
            task.setName(rs.getString("name"));
            task.setGroup(rs.getString("group"));
            task.setTriggerType(rs.getString("triggerType"));
            Map<Object, Object> data = getObjectFromBlob(rs, "data");
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
        }, args.toArray());

        return new Pageable<>(page, DEFAULT_PAGE_SIZE, resultTotal, result);
    }

    public int queryCount(String schedulerName, String name, String group, String state) {
        List<Object> args = new ArrayList<>();
        StringBuilder sqlQueryResultCount = new StringBuilder("SELECT COUNT(1) FROM BS_TRIGGERS t1 JOIN BS_JOB_DETAILS t2 ON t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");
        StringBuilder sqlWhere = new StringBuilder(" WHERE t1.SCHED_NAME = ?");
        args.add(schedulerName);
        if (name != null) {
            sqlWhere.append(" AND t1.TRIGGER_NAME LIKE ?");
            args.add("%" + name + "%");
        }
        if (group != null) {
            sqlWhere.append(" AND t1.TRIGGER_GROUP = ?");
            args.add(group);
        }
        if (state != null) {
            sqlWhere.append(" AND t1.TRIGGER_STATE = ?");
            args.add(state);
        }
        // 查询记录总数
        return jdbcTemplate.queryForObject(sqlQueryResultCount.append(sqlWhere).toString(), Integer.class, args.toArray());
    }

    public List<ExecutingTask> queryExecuting(String schedulerName) {
        List<Object> args = new ArrayList<>();

        StringBuilder sqlQueryResult = new StringBuilder("SELECT t1.SCHED_NAME 'schedulerName',t1.JOB_NAME 'name',t1.JOB_GROUP 'group',t1.TRIGGER_TYPE 'triggerType',t2.JOB_CLASS_NAME 'jobClassName',t1.JOB_DATA 'data',t3.STATE 'state',t1.PREV_FIRE_TIME 'prevFireTime',t1.NEXT_FIRE_TIME 'nextFireTime',t1.START_TIME 'startTime',t1.END_TIME 'endTime',t1.MISFIRE_INSTR 'misfireInstr',t1.DESCRIPTION 'description',t3.FIRED_TIME 'fireTime' FROM BS_TRIGGERS t1 JOIN BS_JOB_DETAILS t2 ON t1.SCHED_NAME = t2.SCHED_NAME AND t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP JOIN BS_FIRED_TRIGGERS t3 ON t1.TRIGGER_NAME = t3.TRIGGER_NAME AND t1.TRIGGER_GROUP = t3.TRIGGER_GROUP");
        StringBuilder sqlWhere = new StringBuilder(" WHERE t3.STATE = 'EXECUTING' AND t1.SCHED_NAME = ?");
        args.add(schedulerName);

        // 查询记录
        sqlQueryResult.append(sqlWhere).append(" ORDER BY t3.FIRED_TIME DESC");

        final List<ExecutingTask> result = new ArrayList<>();


        jdbcTemplate.query(sqlQueryResult.toString(), rs -> {
            ExecutingTask executingTask = new ExecutingTask();
            executingTask.setSchedulerName(rs.getString("schedulerName"));
            executingTask.setName(rs.getString("name"));
            String group = rs.getString("group");
            executingTask.setGroup(group);
            executingTask.setTriggerType(rs.getString("triggerType"));
            Map<Object, Object> data = getObjectFromBlob(rs, "data");
            String execModule = data == null ? null : String.valueOf(data.get(Constants.TRIGGER_DATA_KEY_TASK_MODULE_ID));
            executingTask.setExecModule(execModule);
            executingTask.setPrevFireTime(rs.getLong("prevFireTime"));
            executingTask.setNextFireTime(rs.getLong("nextFireTime"));
            executingTask.setStartTime(rs.getLong("startTime"));
            executingTask.setEndTime(rs.getLong("endTime"));
            executingTask.setMisfireInstr(rs.getInt("misfireInstr"));
            executingTask.setState(rs.getString("state"));
            executingTask.setDescription(rs.getString("description"));
            executingTask.setFiredWay(TaskFiredWay.valueOf(group));
            executingTask.setFiredTime(rs.getLong("fireTime"));
            executingTask.setData(data);
            result.add(executingTask);
        }, args.toArray());

        return result;
    }

    private Map<Object, Object> getObjectFromBlob(ResultSet rs, String colName) {
        try (InputStream binaryInput = rs.getBinaryStream(colName)) {
            if (binaryInput != null) {
                Properties properties = new Properties();
                properties.load(binaryInput);
                return new HashMap<>(properties);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
