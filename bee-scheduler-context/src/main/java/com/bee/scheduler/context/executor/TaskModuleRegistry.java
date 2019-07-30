package com.bee.scheduler.context.executor;

import com.bee.scheduler.core.AbstractTaskModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author weiwei
 */
public class TaskModuleRegistry {
    private static Log logger = LogFactory.getLog(TaskModuleRegistry.class);
    public static final Map<String, AbstractTaskModule> TaskModuleMap = new HashMap<>();

    public static void register(AbstractTaskModule taskModule) {
        logger.info("loaded module : " + taskModule.getId());
        TaskModuleMap.put(taskModule.getId(), taskModule);
    }

    public static AbstractTaskModule get(String taskModuleId) {
        return TaskModuleMap.get(taskModuleId);
    }

    public static void unregister(String taskModuleId) {
        TaskModuleMap.remove(taskModuleId);
    }
}
