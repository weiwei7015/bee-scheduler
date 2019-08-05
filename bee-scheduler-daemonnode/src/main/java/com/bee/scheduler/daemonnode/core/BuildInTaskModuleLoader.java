package com.bee.scheduler.daemonnode.core;

import com.bee.scheduler.context.taskmodule.ClearTaskHistoryTaskModule;
import com.bee.scheduler.context.taskmodule.JustTestTaskModule;
import com.bee.scheduler.core.AbstractTaskModule;

import java.util.ArrayList;
import java.util.List;

public class BuildInTaskModuleLoader implements TaskModuleLoader {
    @Override
    public List<AbstractTaskModule> load() {
        return new ArrayList<AbstractTaskModule>() {{
            add(new JustTestTaskModule());
            add(new ClearTaskHistoryTaskModule());
        }};
    }
}
