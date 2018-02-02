package com.bee.scheduler.core.job;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.utils.DBConnectionManager;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;

/**
 * @author weiwei
 * 用于清除历史任务记录
 */
public class BuildInJobComponent extends JobComponent {
    @Override
    public String getName() {
        return "BuildInJobComponent";
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
        return "用于执行内置任务";
    }

    @Override
    public String getParamTemplate() {
        return "{\r" +
                "    task:''\r" +
                "}";
    }

    @Override
    public boolean run(TaskExecutionContext context) throws Exception {
        JSONObject taskParam = context.getTaskParam();
        TaskExecutionLog taskLogger = context.getLogger();

        String task = taskParam.getString("task");

        if (StringUtils.equals("clear_task_history", task)) {
            // 保留最近5天任务记录
            int keepDays = taskParam.getInteger("keep_days");
            Calendar date_point = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
            date_point.set(Calendar.DAY_OF_MONTH, date_point.get(Calendar.DAY_OF_MONTH) - keepDays);


            Connection connection = DBConnectionManager.getInstance().getConnection(LocalDataSourceJobStore.TX_DATA_SOURCE_PREFIX + context.getJobExecutionContext().getScheduler().getSchedulerName());
            String sql = "DELETE FROM BS_TASK_HISTORY WHERE FIRED_TIME <= ?";

            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setLong(1, date_point.getTimeInMillis());
                int result = preparedStatement.executeUpdate();
                taskLogger.info("任务执行结果 -> 清除历史任务记录完毕，已成功清除 " + result + " 条记录");
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
        }
        return true;
    }
}