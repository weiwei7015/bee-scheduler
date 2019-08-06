package com.bee.scheduler.context.listener;

import com.bee.scheduler.context.task.TaskExecutionLogger;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;

/**
 * @author weiwei
 */
public class BindingTaskLoggerOnThreadLocalListener extends TaskListenerSupport {

    @Override
    public String getName() {
        return "BindingTaskLoggerOnThreadLocalListener";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        ThreadLocalTaskLoggerAppender.TaskExecutionLoggerThreadLocal.set(new TaskExecutionLogger());
    }
}
