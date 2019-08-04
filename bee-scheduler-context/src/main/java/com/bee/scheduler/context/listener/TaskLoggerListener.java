package com.bee.scheduler.context.listener;

import com.bee.scheduler.context.TaskLoggerAppender;
import com.bee.scheduler.core.TaskExecutionContext;
import org.quartz.Scheduler;

/**
 * @author weiwei
 */
public class TaskLoggerListener extends AbstractTaskListener {

    @Override
    public String getName() {
        return "TaskLoggerListener";
    }

//    @Override
//    protected void taskToBeExecuted(TaskExecutionContext context, Scheduler scheduler) {
//        TaskLoggerAppender.TaskExecutionLoggerThreadLocal.set(context.getLogger());
//    }
//
//    @Override
//    protected void taskWasExecuted(TaskExecutionContext context, TaskExecutionResult result, Scheduler scheduler, JobExecutionException jobException) {
//        TaskLoggerAppender.TaskExecutionLoggerThreadLocal.set(null);
//    }
//
//    @Override
//    protected void taskExecutionVetoed(TaskExecutionContext context, Scheduler scheduler) {
//        TaskLoggerAppender.TaskExecutionLoggerThreadLocal.set(null);
//    }


    @Override
    protected void taskTriggerFired(TaskExecutionContext context, Scheduler scheduler) {
        TaskLoggerAppender.TaskExecutionLoggerThreadLocal.set(context.getLogger());
    }
}
