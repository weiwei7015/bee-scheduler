package com.bee.scheduler.context.executor.module;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.BasicExecutionResult;
import com.bee.scheduler.core.ExecutionContext;
import com.bee.scheduler.core.ExecutorModule;
import org.apache.commons.logging.Log;

/**
 * @author weiwei
 * 仅用于测试目的组件，简单地将content参数输出到日志
 */
public class JustTestTaskModule implements ExecutorModule {
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
    public BasicExecutionResult exec(ExecutionContext context) throws Exception {
        JSONObject taskParam = context.getParam();
        Log logger = context.getLogger();

        String content = taskParam.getString("content");
        logger.info("content: [" + content + "]");
        JSONObject data = new JSONObject();
        data.put("content", content);
        return BasicExecutionResult.success(data);
    }

}