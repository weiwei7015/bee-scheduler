package com.bee.scheduler.core.job;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLog;

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
        return "weiwei";
    }

    @Override
    public String getDescription() {
        return "该组件提供运行服务端脚本的功能";
    }

    @Override
    public String getParamTemplate() {
        StringBuilder t = new StringBuilder();
        t.append("{\r");
        t.append("    shell:''\r");
        t.append("}");
        return t.toString();
    }

    @Override
    public boolean run(TaskExecutionContext context) throws Exception {
        JSONObject taskParam = context.getTaskParam();
        TaskExecutionLog taskLogger = context.getLogger();

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
        taskLogger.info("任务执行成功 -> " + back.toString());

        return true;
    }
}
