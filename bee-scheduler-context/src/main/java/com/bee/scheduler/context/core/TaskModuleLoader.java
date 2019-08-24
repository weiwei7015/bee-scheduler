package com.bee.scheduler.context.core;

import com.bee.scheduler.core.ExecutorModule;

import java.util.List;

public interface TaskModuleLoader {
    List<ExecutorModule> load() throws Exception;
}