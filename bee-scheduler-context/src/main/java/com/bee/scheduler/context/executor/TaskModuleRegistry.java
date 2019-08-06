package com.bee.scheduler.context.executor;

import com.bee.scheduler.core.ExecutorModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author weiwei
 */
public class TaskModuleRegistry {
    private static Log logger = LogFactory.getLog(TaskModuleRegistry.class);
    public static final Map<String, ExecutorModule> TaskModuleMap = new HashMap<>();

    public static void register(ExecutorModule taskModule) {
        logger.info("loaded module : " + taskModule.getId());
        TaskModuleMap.put(taskModule.getId(), taskModule);
    }

    public static ExecutorModule get(String taskModuleId) {
        return TaskModuleMap.get(taskModuleId);
    }

    public static void unregister(String taskModuleId) {
        TaskModuleMap.remove(taskModuleId);
    }
}
