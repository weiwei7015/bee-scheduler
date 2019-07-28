package com.bee.scheduler.context.exception;

import org.quartz.SchedulerException;

/**
 * @author weiwei
 */
public class TaskSchedulerException extends Exception {
    private SchedulerException schedulerException;

    public TaskSchedulerException(SchedulerException schedulerException) {
        this.schedulerException = schedulerException;
    }
}
