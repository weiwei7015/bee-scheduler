package com.bee.scheduler.context.listener;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.util.CachingDateFormatter;
import com.bee.scheduler.core.TaskExecutionLogger;
import org.apache.commons.lang3.StringUtils;

/**
 * @author weiwei
 */
public class ThreadLocalTaskLoggerAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    public static final ThreadLocal<TaskExecutionLogger> TaskExecutionLoggerThreadLocal = new ThreadLocal<>();
    private CachingDateFormatter cachingDateFormatter = new CachingDateFormatter("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void append(ILoggingEvent eventObject) {
        TaskExecutionLogger taskExecutionLogger = TaskExecutionLoggerThreadLocal.get();
        if (taskExecutionLogger != null) {
            taskExecutionLogger.appendLine("[" + eventObject.getLevel() + "] " + "[" + cachingDateFormatter.format(eventObject.getTimeStamp()) + "] " + eventObject.getMessage());
            if (eventObject.getThrowableProxy() != null) {
                taskExecutionLogger.appendLine(StringUtils.substring(ThrowableProxyUtil.asString(eventObject.getThrowableProxy()), 0, 600));
            }
        }
    }

    public static String getLogContent() {
        TaskExecutionLogger taskExecutionLogger = TaskExecutionLoggerThreadLocal.get();
        if (taskExecutionLogger != null) {
            return taskExecutionLogger.getLog();
        } else {
            return "";
        }
    }
}
