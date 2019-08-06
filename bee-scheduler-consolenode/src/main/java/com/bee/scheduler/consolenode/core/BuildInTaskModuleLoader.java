package com.bee.scheduler.consolenode.core;

import com.bee.scheduler.context.executor.module.ClearTaskHistoryTaskModule;
import com.bee.scheduler.context.executor.module.HttpExcutorModule;
import com.bee.scheduler.context.executor.module.JustTestTaskModule;
import com.bee.scheduler.context.executor.module.ShellTaskModule;
import com.bee.scheduler.core.ExecutorModule;

import java.util.ArrayList;
import java.util.List;

public class BuildInTaskModuleLoader implements TaskModuleLoader {
    @Override
    public List<ExecutorModule> load() {
        return new ArrayList<ExecutorModule>() {{
            add(new ShellTaskModule());
            add(new HttpExcutorModule());
            add(new JustTestTaskModule());
            add(new ClearTaskHistoryTaskModule());
        }};
    }
}
