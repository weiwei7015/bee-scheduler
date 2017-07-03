package com.bee.scheduler.core.job;

import com.alibaba.fastjson.JSONObject;
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
        StringBuilder t = new StringBuilder();
        t.append("{\r");
        t.append("	\"url\":\"\",\r");
        t.append("	\"method\":\"get\",\r");
        t.append("	\"timeout\":5000,\r");
        t.append("	\"cookies\":[\r");
        t.append("		{\"n\":\"\",\"v\":\"\"}\r");
        t.append("	]\r");
        t.append("}");
        return t.toString();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        JobExecutionContextHelper.appendExecLog(context, "开始执行任务 -> " + jobDetail.getKey());
        try {
            // 构建请求
            JSONObject params = getTaskParam(context);

            JobExecutionContextHelper.appendExecLog(context, "任务参数 -> " + params.toString());


            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

            RequestConfig.Builder requesetConfigBuilder = RequestConfig.custom();
            requesetConfigBuilder.setConnectTimeout(params.getIntValue("timeout"));
            RequestConfig requestConfig = requesetConfigBuilder.build();

            httpClientBuilder.setDefaultRequestConfig(requestConfig);

            CloseableHttpClient client = httpClientBuilder.build();


            String uri = params.getString("url");
            HttpUriRequest request;
            if (StringUtils.equalsIgnoreCase("post", params.getString("method"))) {
                request = new HttpPost(uri);
            } else {
                request = new HttpGet(uri);
            }
            // 添加cookie
            StringBuilder cookiesStr = new StringBuilder("");
            if (params.containsKey("cookies")) {
                for (Object cookie : params.getJSONArray("cookies").toArray()) {
                    JSONObject cookieJson = JSONObject.parseObject(cookie.toString());
                    cookiesStr.append(cookieJson.getString("n")).append("=").append(cookieJson.getString("v")).append("; ");
                }
                Header cookieHeader = new BasicHeader("Cookie", cookiesStr.toString());
                request.addHeader(cookieHeader);
            }
            try {
                // 发起请求
                CloseableHttpResponse response = client.execute(request);
                JobExecutionContextHelper.appendExecLog(context, "执行完成 -> " + response.getStatusLine());
            } catch (Exception e) {
                throw new JobExecutionException(e);
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

}
