package com.bee.scheduler.core.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.JobExecutionContextUtil;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;

/**
 * @author weiwei 用于发起HTTP请求
 */
public class HttpClientJobComponent extends JobComponent {

    @Override
    public String getName() {
        return "HttpClientJob";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getAuthor() {
        return "vivi";
    }

    @Override
    public String getDescription() {
        return "用于发起HTTP请求";
    }

    @Override
    public String getParamTemplate() {
        return "{\r" +
                "    url:'',\r" +
                "    method:'get',\r" +
                "    timeout:5000,\r" +
                "    cookies:[\r" +
                "        {n:'',v:''}\r" +
                "    ]\r" +
                "}";
    }

    @Override
    public boolean run(TaskExecutionContext context) throws Exception {
        JSONObject taskParam = context.getTaskParam();
        TaskExecutionLog taskLogger = context.getLogger();

        String uri = taskParam.getString("url");
        int timeout = taskParam.getIntValue("timeout");
        String method = taskParam.getString("method");
        JSONArray cookies = taskParam.getJSONArray("cookies");


        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        RequestConfig.Builder requesetConfigBuilder = RequestConfig.custom();
        requesetConfigBuilder.setConnectTimeout(timeout);
        RequestConfig requestConfig = requesetConfigBuilder.build();

        httpClientBuilder.setDefaultRequestConfig(requestConfig);

        CloseableHttpClient client = httpClientBuilder.build();


        HttpUriRequest request;
        if (StringUtils.equalsIgnoreCase("post", method)) {
            request = new HttpPost(uri);
        } else {
            request = new HttpGet(uri);
        }
        // 添加cookie
        StringBuilder cookiesStr = new StringBuilder("");
        if (cookies != null && cookies.size() > 0) {
            for (Object cookie : cookies.toArray()) {
                JSONObject cookieJson = JSONObject.parseObject(cookie.toString());
                cookiesStr.append(cookieJson.getString("n")).append("=").append(cookieJson.getString("v")).append("; ");
            }
            Header cookieHeader = new BasicHeader("Cookie", cookiesStr.toString());
            request.addHeader(cookieHeader);
        }
        try {
            // 发起请求
            CloseableHttpResponse response = client.execute(request);
            taskLogger.info("任务执行结果:" + response.getStatusLine());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                taskLogger.error(e.getMessage(), e);
            }
        }
        return true;
    }
}
