package com.bee.scheduler.core.job;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLog;
import org.apache.commons.lang3.StringUtils;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author weiwei 用于发起HTTP请求
 */
public class HttpClientJobComponent extends JobComponent {

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
    public boolean run(TaskExecutionContext context) throws Exception {
        JSONObject taskParam = context.getTaskParam();
        TaskExecutionLog taskLogger = context.getLogger();

        String url = taskParam.getString("url");
        int timeout = taskParam.getIntValue("timeout");
        String method = StringUtils.upperCase(taskParam.getString("method"));


        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("user-agent", "BeeScheduler");
        connection.setRequestMethod(method);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.connect();

        int result = connection.getResponseCode();

        taskLogger.info("任务执行成功 -> response status code:" + result);
        return true;
    }
}
