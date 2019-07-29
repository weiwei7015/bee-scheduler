package com.bee.scheduler.context.exception;

/**
 * @author weiwei
 */
public class TaskSchedulerException extends Exception {

    public TaskSchedulerException() {
    }

    public TaskSchedulerException(String message) {
        super(message);
    }

    public TaskSchedulerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TaskSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskSchedulerException(Throwable cause) {
        super(cause);
    }
}
