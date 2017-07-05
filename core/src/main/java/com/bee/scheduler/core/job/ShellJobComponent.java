package com.bee.scheduler.core.job;

import com.alibaba.fastjson.JSONObject;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author weiwei 该组件提供运行服务端脚本的功能
 */
public class ShellJobComponent extends JobComponent {
    @Override
    public String getName() {
        return "ShellJobComponent";
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
        return "该组件提供运行服务端脚本的功能";
    }

    @Override
    public String getParamTemplate() {
        StringBuilder t = new StringBuilder();
        t.append("{\r");
        t.append("  shell:\"\"\r");
        t.append("}");
        return t.toString();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        JobExecutionContextHelper.appendExecLog(context, "开始执行任务 -> " + jobDetail.getKey());
        try {
            JSONObject taskParam = getTaskParam(context);
            JobExecutionContextHelper.appendExecLog(context, "任务参数 -> " + taskParam.toString());

            String shell = taskParam.getString("shell");

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(shell);
            InputStream stderr = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder back = new StringBuilder();
            while ((line = br.readLine()) != null) {
                back.append(line + "\r");
            }

            JobExecutionContextHelper.appendExecLog(context, "执行完成 -> " + back.toString());
        } catch (Exception e) {
            JobExecutionException jobExecutionException = new JobExecutionException(e);
            throw jobExecutionException;
        }

    }

}
