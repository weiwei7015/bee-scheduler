package com.bee.scheduler.core.job;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author weiwei 该组件提供链接Mysql数据库执行Sql的功能
 */
public class SqlExecutorJobComponent extends JobComponent {
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
        StringBuilder t = new StringBuilder();
        t.append("{\r");
        t.append("    url:'jdbc:mysql://localhost:3306/mysql',\r");
        t.append("    type:'query',\r");
        t.append("    sql:''\r");
        t.append("}");
        return t.toString();
    }

    @Override
    public boolean run(TaskExecutionContext context) throws Exception {
        JSONObject taskParam = context.getTaskParam();
        TaskExecutionLog taskLogger = context.getLogger();

        String url = taskParam.getString("url");
        String type = taskParam.getString("type");
        String sql = taskParam.getString("sql");

        if (url.startsWith("jdbc:mysql")) {
            Class.forName("com.mysql.jdbc.Driver");
        } else {
            throw new RuntimeException("暂不支持该数据库[" + url + "]");
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DriverManager.getConnection(url);
            preparedStatement = connection.prepareStatement(sql);
            if ("query".equalsIgnoreCase(type)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                int count = 0;
                while (resultSet.next()) {
                    count = count + 1;
                }
                taskLogger.info("任务执行成功 -> 查询到" + count + "条记录");
            } else if ("update".equalsIgnoreCase(type)) {
                int result = preparedStatement.executeUpdate();
                taskLogger.info("任务执行成功 -> 影响记录总数：" + result);
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }

        return true;
    }

}
