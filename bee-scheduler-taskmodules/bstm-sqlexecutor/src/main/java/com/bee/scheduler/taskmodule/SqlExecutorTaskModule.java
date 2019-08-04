package com.bee.scheduler.taskmodule;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.AbstractTaskModule;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLogger;
import com.bee.scheduler.core.TaskExecutionResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author weiwei 该组件提供链接Mysql数据库执行Sql的功能
 */
public class SqlExecutorTaskModule extends AbstractTaskModule {
    private Log logger = LogFactory.getLog(SqlExecutorTaskModule.class);

    @Override
    public String getId() {
        return "SqlExecutor";
    }

    @Override
    public String getName() {
        return "SqlExecutor";
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
        return "该组件用于运行SQL脚本(目前仅支持Mysql)";
    }

    @Override
    public String getParamTemplate() {
        return "{\r" +
                "    url:'jdbc:mysql://localhost:3306/mysql',\r" +
                "    type:'query',\r" +
                "    sql:''\r" +
                "}";
    }

    @Override
    public TaskExecutionResult run(TaskExecutionContext context) throws Exception {
        JSONObject taskParam = context.getParam();

        String url = taskParam.getString("url");
        String type = taskParam.getString("type");
        String sql = taskParam.getString("sql");

        if (url.startsWith("jdbc:mysql")) {
            Class.forName("com.mysql.jdbc.Driver");
        } else {
            throw new RuntimeException("暂不支持该数据库[" + url + "]");
        }


        JSONObject data = new JSONObject();
        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            if ("query".equalsIgnoreCase(type)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                int affectedRowCount = 0;
                while (resultSet.next()) {
                    affectedRowCount = affectedRowCount + 1;
                }
                logger.info("任务执行成功 -> 查询到" + affectedRowCount + "条记录");
                data.put("affected_row_count", affectedRowCount);
            } else if ("update".equalsIgnoreCase(type)) {
                int affectedRowCount = preparedStatement.executeUpdate();
                logger.info("任务执行成功 -> 影响记录总数：" + affectedRowCount);
                data.put("affected_row_count", affectedRowCount);
            }
        }

        return TaskExecutionResult.success(data);
    }

}
