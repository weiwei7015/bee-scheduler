package com.bee.scheduler.daemonnode.core;

import com.bee.scheduler.core.AbstractTaskModule;

import java.util.List;

public interface TaskModuleLoader {
    List<AbstractTaskModule> load() throws Exception;
}