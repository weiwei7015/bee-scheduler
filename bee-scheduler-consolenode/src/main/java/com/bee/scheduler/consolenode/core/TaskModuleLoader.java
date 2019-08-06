package com.bee.scheduler.consolenode.core;

import com.bee.scheduler.core.ExecutorModule;

import java.util.List;

public interface TaskModuleLoader {
    List<ExecutorModule> load() throws Exception;
}