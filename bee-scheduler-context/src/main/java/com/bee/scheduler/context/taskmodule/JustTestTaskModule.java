package com.bee.scheduler.context.taskmodule;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.AbstractTaskModule;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author weiwei
 * 仅用于测试目的组件，简单地将content参数输出到日志
 */
public class JustTestTaskModule extends AbstractTaskModule {
    private Log logger = LogFactory.getLog(JustTestTaskModule.class);

    @Override
    public String getId() {
        return "JustTestTaskModule";
    }

    @Override
    public String getName() {
        return "JustTestTaskModule";
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
        return "仅用于测试目的组件，简单地将content参数输出到日志";
    }

    @Override
    public String getParamTemplate() {
        return "{\r" +
                "    content:''\r" +
                "}";
    }

    @Override
    public TaskExecutionResult run(TaskExecutionContext context) throws Exception {
        JSONObject taskParam = context.getParam();
        String content = taskParam.getString("content");
        logger.info("content: [" + content + "]");
        JSONObject data = new JSONObject();
        data.put("content", content);
        return TaskExecutionResult.success(data);
    }

}