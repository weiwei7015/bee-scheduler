package com.bee.scheduler.context.executor;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.ExecutionResult;
import com.bee.scheduler.core.ExecutionContext;
import com.bee.scheduler.core.ExecutorModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author weiwei 用于发起HTTP请求
 */
public class HttpExcutorModule implements ExecutorModule {
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
                "    url:\"\",\r" +
                "    method:\"get\",\r" +
                "    timeout:5000,\r" +
                "    headers:\"{name:'value'}\",\r" +
                "    body:\"\"\r" +
                "}";
    }

    @Override
    public ExecutionResult exec(ExecutionContext context) throws Exception {
        JSONObject taskParam = context.getParam();
        Log logger = context.getLogger();

        String url = taskParam.getString("url");
        int timeout = taskParam.getIntValue("timeout");
        String method = StringUtils.upperCase(taskParam.getString("method"));
        String body = taskParam.getString("body");

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("user-agent", "BeeScheduler");
        connection.setRequestMethod(method);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        //请求头
        Object headers = taskParam.get("headers");
        if (headers instanceof JSONObject) {
            ((JSONObject) headers).forEach((k, v) -> {
                connection.setRequestProperty(k, String.valueOf(v));
            });
        }

        //body
        if (body != null) {
            try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                out.append(body);
                out.flush();
            }
        }

        //建立连接
        connection.connect();

        //响应状态码
        int responseStatus = connection.getResponseCode();
        //响应内容
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
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

        logger.info("response status: " + responseStatus);

        JSONObject data = new JSONObject();
        data.put("response_status", responseStatus);
        data.put("response_content", result.toString());
        if (responseStatus == 200) {
            return ExecutionResult.success(data);
        } else {
            return ExecutionResult.fail(data);
        }
    }
}
