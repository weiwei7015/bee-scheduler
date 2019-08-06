package com.bee.scheduler.context.exception;

/**
 * @author weiwei
 */
public class ExecutorModuleNotFountException extends RuntimeException {
    private final String executorModuleId;

    public ExecutorModuleNotFountException(String executorModuleId) {
        this.executorModuleId = executorModuleId;
    }

    public String getExecutorModuleId() {
        return executorModuleId;
    }
}
