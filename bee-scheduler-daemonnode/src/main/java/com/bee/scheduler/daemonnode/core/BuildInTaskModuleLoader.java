package com.bee.scheduler.daemonnode.core;

import com.bee.scheduler.context.executor.ClearTaskHistoryTaskModule;
import com.bee.scheduler.context.executor.JustTestTaskModule;
import com.bee.scheduler.core.ExecutorModule;

import java.util.ArrayList;
import java.util.List;

public class BuildInTaskModuleLoader implements TaskModuleLoader {
    @Override
    public List<ExecutorModule> load() {
        return new ArrayList<ExecutorModule>() {{
            add(new JustTestTaskModule());
            add(new ClearTaskHistoryTaskModule());
        }};
    }
}
