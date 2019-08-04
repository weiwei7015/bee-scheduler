package com.bee.scheduler.core;

/**
 * @author weiwei 任务组件接口
 */
public abstract class AbstractTaskModule {
    /**
     * ID
     */
    public abstract String getId();

    /**
     * 名称
     */
    public abstract String getName();

    /**
     * 描述
     */
    public abstract String getDescription();

    /**
     * 参数模板：json格式
     */
    public abstract String getParamTemplate();

    /**
     * 版本号
     */
    public abstract String getVersion();

    /**
     * 作者
     */
    public abstract String getAuthor();

    public abstract TaskExecutionResult run(TaskExecutionContext context) throws Exception;
}