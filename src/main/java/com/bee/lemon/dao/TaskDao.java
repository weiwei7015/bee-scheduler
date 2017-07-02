package com.bee.lemon.dao;

import com.bee.lemon.model.Pageable;
import com.bee.lemon.model.Task;
import com.bee.lemon.model.TaskHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @author weiwei
 */
@Repository
public class TaskDao extends DaoBase {

    public Pageable<Task> query(String schedulerName, String name, String group, String state, int page) {
        List<Object> args = new ArrayList<>();
        StringBuilder sqlQueryResultCount = new StringBuilder("SELECT COUNT(1) FROM QRTZ_TRIGGERS t1 JOIN qrtz_job_details t2 ON t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");
        StringBuilder sqlQueryResult = new StringBuilder("SELECT t1.TRIGGER_NAME 'name',t1.TRIGGER_GROUP 'group',t1.TRIGGER_TYPE 'triggerType',t2.JOB_CLASS_NAME 'jobComponent',t1.JOB_DATA 'data',t1.TRIGGER_STATE 'state',t1.PREV_FIRE_TIME 'prevFireTime',t1.NEXT_FIRE_TIME 'nextFireTime',t1.START_TIME 'startTime',t1.END_TIME 'endTime',t1.MISFIRE_INSTR 'misfireInstr',t1.DESCRIPTION 'description' FROM QRTZ_TRIGGERS t1 JOIN QRTZ_JOB_DETAILS t2 ON t1.SCHED_NAME = t2.SCHED_NAME AND t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP");

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
        Integer resultTotal = jdbcTemplate.queryForObject(sqlQueryResultCount.append(sqlWhere).toString(), Integer.class, args.toArray());
        // 查询记录
        sqlQueryResult.append(sqlWhere).append(" ORDER BY t1.NEXT_FIRE_TIME ASC LIMIT ?,?");
        args.add((page - 1) * getPageSize());
        args.add(getPageSize());

        List<Task> result = new ArrayList<>();


        jdbcTemplate.query(sqlQueryResult.toString(), rs -> {
            Task task = new Task();
            task.setName(rs.getString("name"));
            task.setGroup(rs.getString("group"));
            task.setTriggerType(rs.getString("triggerType"));
            String jobComponent = rs.getString("jobComponent");
            task.setJobComponent(jobComponent.substring(jobComponent.lastIndexOf(".") + 1));
            task.setPrevFireTime(rs.getLong("prevFireTime"));
            task.setNextFireTime(rs.getLong("nextFireTime"));
            task.setStartTime(rs.getLong("startTime"));
            task.setEndTime(rs.getLong("endTime"));
            task.setMisfireInstr(rs.getInt("misfireInstr"));
            task.setState(rs.getString("state"));
            task.setDescription(rs.getString("description"));

            try {
                Blob blobLocator = rs.getBlob("data");
                if (blobLocator != null) {
                    InputStream binaryInput = blobLocator.getBinaryStream();

                    Properties properties = new Properties();
                    if (binaryInput != null) {
                        try {
                            properties.load(binaryInput);
                        } finally {
                            binaryInput.close();
                        }
                    }
                    Map<Object, Object> data = new HashMap<>(properties);
                    task.setData(data);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            result.add(task);
        }, args.toArray());

        return new Pageable<>(page, getPageSize(), resultTotal, result);
    }

    public Task get(String name, String group) {
        String sql = "SELECT t1.TRIGGER_NAME 'name',t1.TRIGGER_GROUP 'group',t2.JOB_CLASS_NAME 'jobComponent',t1.JOB_DATA 'data',t1.DESCRIPTION 'description' FROM QRTZ_TRIGGERS t1 JOIN qrtz_job_details t2 ON t1.JOB_NAME = t2.JOB_NAME AND t1.JOB_GROUP = t2.JOB_GROUP where t1.SCHED_NAME = ? and t1.TRIGGER_NAME = ? and t1.TRIGGER_GROUP = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Task.class), "DefaultQuartzScheduler", name, group);
    }


}
