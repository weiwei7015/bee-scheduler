package com.bee.scheduler.taskmodule;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.AbstractTaskModule;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLogger;
import com.bee.scheduler.core.TaskExecutionResult;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author weiwei 用于发起HTTP请求
 */
public class HttpClientTaskModule extends AbstractTaskModule {

    public String getId() {
        return "HttpJob";
    }

    @Override
    public String getName() {
        return "HttpJob";
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
        return "用于执行HTTP请求";
    }

    @Override
    public String getParamTemplate() {
        return "{\r" +
                "    url:'',\r" +
                "    method:'get',\r" +
                "    timeout:5000\r" +
                "}";
    }

    @Override
    public TaskExecutionResult run(TaskExecutionContext context) throws Exception {
        JSONObject taskParam = context.getParam();
        TaskExecutionLogger taskLogger = context.getLogger();

        String url = taskParam.getString("url");
        int timeout = taskParam.getIntValue("timeout");
        String method = StringUtils.upperCase(taskParam.getString("method"));

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("user-agent", "BeeScheduler");
        connection.setRequestMethod(method);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.connect();

        //响应状态码
        int responseStatus = connection.getResponseCode();
        //响应内容
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        StringBuilder result = new StringBuilder();
        String temp;
        while ((temp = bufferedReader.readLine()) != null) {
            if (result.length() >= 600) {
                result.append("...").append("\r");
                break;
            }
            result.append(temp).append("\r");
        }
        bufferedReader.close();

        taskLogger.info("response status: " + responseStatus);

        JSONObject data = new JSONObject();
        data.put("response_status", responseStatus);
        data.put("response_content", result.toString());
        if (responseStatus == 200) {
            return TaskExecutionResult.success(data);
        } else {
            return TaskExecutionResult.fail(data);
        }
    }
}
