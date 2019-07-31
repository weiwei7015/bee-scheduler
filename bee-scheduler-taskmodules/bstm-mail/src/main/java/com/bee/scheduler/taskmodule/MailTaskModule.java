package com.bee.scheduler.taskmodule;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.AbstractTaskModule;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLogger;
import org.apache.commons.lang3.StringUtils;

/**
 * @author weiwei 用于发起HTTP请求
 */
public class MailTaskModule extends AbstractTaskModule {

    public String getId() {
        return "MailTask";
    }

    @Override
    public String getName() {
        return "MailTask";
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
        return "用于发送邮件";
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
        JSONObject taskParam = context.getParam();
        TaskExecutionLogger taskLogger = context.getLogger();

        String url = taskParam.getString("url");
        int timeout = taskParam.getIntValue("timeout");
        String method = StringUtils.upperCase(taskParam.getString("method"));

        return true;
    }

    public static void main(String[] args) {

    }
}
