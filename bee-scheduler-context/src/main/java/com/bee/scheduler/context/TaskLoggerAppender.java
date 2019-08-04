package com.bee.scheduler.context;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.util.CachingDateFormatter;
import com.bee.scheduler.core.TaskExecutionLogger;

/**
 * @author weiwei
 */
public class TaskLoggerAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    public static final ThreadLocal<TaskExecutionLogger> TaskExecutionLoggerThreadLocal = new ThreadLocal<>();
    private CachingDateFormatter cachingDateFormatter = new CachingDateFormatter("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void append(ILoggingEvent eventObject) {
        TaskExecutionLogger taskExecutionLogger = TaskExecutionLoggerThreadLocal.get();
        if (taskExecutionLogger != null) {
            taskExecutionLogger.appendLine("[" + eventObject.getLevel() + "] " + "[" + cachingDateFormatter.format(eventObject.getTimeStamp()) + "] " + eventObject.getMessage());
        }
    }
}
