package com.bee.scheduler.context.taskmodules;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.AbstractTaskModule;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.utils.DBConnectionManager;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author weiwei
 * 用于清除历史任务记录
 */
public class ClearTaskHistoryTaskModule extends AbstractTaskModule {
    @Override
    public String getId() {
        return "ClearTaskHistory";
    }

    @Override
    public String getName() {
        return "ClearTaskHistory";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getAuthor() {
        return "weiwei";
    }

    @Override
    public String getDescription() {
        return "用于清除历史任务记录";
    }

    @Override
    public String getParamTemplate() {
        return "{\r" +
                "    keep_days:'5',\r" +
                "    task_group:'',\r" +
                "    task_name:'',\r" +
                "    fired_way:'',\r" +
                "    exec_state:''\r" +
                "}";
    }

    @Override
    public boolean run(TaskExecutionContext context) throws Exception {
        JSONObject taskParam = context.getParam();
        TaskExecutionLogger taskLogger = context.getLogger();

        // 保留最近几天的任务记录
        Integer keepDays = taskParam.getInteger("keep_days");
        if (keepDays == null) {
            taskLogger.error("缺少必须参数:keep_days");
            return false;
        }
        if (keepDays < 0) {
            taskLogger.error("任务参数有误:keep_days");
            return false;
        }
        String taskGroup = taskParam.getString("task_group");
        String taskName = taskParam.getString("task_name");
        String firedWay = taskParam.getString("fired_way");
        String execState = taskParam.getString("exec_state");

        Calendar datePoint = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
        datePoint.set(Calendar.DAY_OF_MONTH, datePoint.get(Calendar.DAY_OF_MONTH) - keepDays);

        Connection connection = DBConnectionManager.getInstance().getConnection(LocalDataSourceJobStore.TX_DATA_SOURCE_PREFIX + context.getSchedulerName());
        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM BS_TASK_HISTORY WHERE FIRED_TIME <= ?");

        ArrayList<String> whereConditions = new ArrayList<>();
        ArrayList<String> args = new ArrayList<>();
        if (StringUtils.isNotEmpty(taskGroup)) {
            whereConditions.add("TASK_GROUP = ?");
            args.add(taskGroup);
        }
        if (StringUtils.isNotEmpty(taskName)) {
            whereConditions.add("TASK_NAME = ?");
            args.add(taskName);
        }
        if (StringUtils.isNotEmpty(firedWay)) {
            whereConditions.add("FIRED_WAY = ?");
            args.add(firedWay);
        }
        if (StringUtils.isNotEmpty(execState)) {
            whereConditions.add("EXEC_STATE = ?");
            args.add(execState);
        }
        if (whereConditions.size() > 0) {
            sqlBuilder.append(" AND ").append(String.join(" AND ", whereConditions));
        }
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sqlBuilder.toString());
            preparedStatement.setLong(1, datePoint.getTimeInMillis());
            if (args.size() > 0) {
                for (int i = 0; i < args.size(); i++) {
                    preparedStatement.setString(i + 2, args.get(i));
                }
            }
            int result = preparedStatement.executeUpdate();
            taskLogger.info("任务执行结果：清除历史任务记录完毕，已成功清除 " + result + " 条记录");
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return true;
    }
}