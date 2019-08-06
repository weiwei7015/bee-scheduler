package com.bee.scheduler.core;

/**
 * @author weiwei 任务组件接口
 */
public interface ExecutorModule {
    String getId();

    String getName();

    String getDescription();

    String getParamTemplate();

    String getVersion();

    String getAuthor();

    ExecutionResult exec(ExecutionContext context) throws Exception;
}