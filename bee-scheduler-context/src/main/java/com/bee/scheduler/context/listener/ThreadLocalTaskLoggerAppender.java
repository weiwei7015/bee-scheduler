package com.bee.scheduler.context.listener;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.util.CachingDateFormatter;
import com.bee.scheduler.context.task.TaskExecutionLogger;

/**
 * @author weiwei
 */
public class ThreadLocalTaskLoggerAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    public static final ThreadLocal<TaskExecutionLogger> TaskExecutionLoggerThreadLocal = new ThreadLocal<>();
    private CachingDateFormatter cachingDateFormatter = new CachingDateFormatter("yyyy-MM-dd HH:mm:ss");

    public static String getLogContent() {
        TaskExecutionLogger taskExecutionLogger = TaskExecutionLoggerThreadLocal.get();
        if (taskExecutionLogger != null) {
            return taskExecutionLogger.getLog();
        } else {
            return "";
        }
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        TaskExecutionLogger taskExecutionLogger = TaskExecutionLoggerThreadLocal.get();
        if (taskExecutionLogger != null) {
            taskExecutionLogger.appendLine("[" + eventObject.getLevel() + "] " + "[" + cachingDateFormatter.format(eventObject.getTimeStamp()) + "] " + eventObject.getMessage());
            if (eventObject.getThrowableProxy() != null) {
                taskExecutionLogger.appendLine(ThrowableProxyUtil.asString(eventObject.getThrowableProxy()));
            }
        }
    }
}
