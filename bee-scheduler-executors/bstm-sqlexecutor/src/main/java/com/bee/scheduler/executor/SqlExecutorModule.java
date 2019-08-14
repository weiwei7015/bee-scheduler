package com.bee.scheduler.executor;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.ExecutionContext;
import com.bee.scheduler.core.ExecutionException;
import com.bee.scheduler.core.ExecutionResult;
import com.bee.scheduler.core.ExecutorModule;
import org.apache.commons.logging.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author weiwei 该组件提供链接Mysql数据库执行Sql的功能
 */
public class SqlExecutorModule implements ExecutorModule {
    @Override
    public String getId() {
        return "SqlExecutorModule";
    }

    @Override
    public String getName() {
        return "SqlExecutorModule";
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
                "    \"url\":\"jdbc:mysql://localhost:3306/mysql\",\r" +
                "    \"type\":\"query\",\r" +
                "    \"sql\":\"\"\r" +
                "}";
    }

    @Override
    public ExecutionResult exec(ExecutionContext context) throws Exception {
        JSONObject taskParam = context.getParam();
        Log logger = context.getLogger();

        String url = taskParam.getString("url");
        String type = taskParam.getString("type");
        String sql = taskParam.getString("sql");

        if (url.startsWith("jdbc:mysql")) {
            Class.forName("com.mysql.jdbc.Driver");
        } else {
            throw new ExecutionException("暂不支持该数据库[" + url + "]");
        }


        JSONObject data = new JSONObject();
        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            if ("query".equalsIgnoreCase(type)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                int columnCount = preparedStatement.getMetaData().getColumnCount();
                int affectedRowCount = 0;

                StringBuilder resultBuilder = new StringBuilder();
                while (resultSet.next()) {
                    affectedRowCount = affectedRowCount + 1;
                    if (resultBuilder.length() > 0) {
                        resultBuilder.append("\r");
                    }
                    for (int i = 1; i <= columnCount; i++) {
                        resultBuilder.append(resultSet.getString(i));
                        if (i < columnCount) {
                            resultBuilder.append(",");
                        }
                    }
                }

                logger.info("任务执行成功 -> 查询到" + affectedRowCount + "条记录");
                logger.info("result:\r" + resultBuilder.toString());

                data.put("affected_row_count", affectedRowCount);
                data.put("result", resultBuilder.toString());
            } else if ("update".equalsIgnoreCase(type)) {
                int affectedRowCount = preparedStatement.executeUpdate();
                logger.info("任务执行成功 -> 影响记录总数：" + affectedRowCount);
                data.put("affected_row_count", affectedRowCount);
            }
        }

        return ExecutionResult.success(data);
    }

}
