package com.bee.scheduler.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author weiwei
 */
public class TaskExecutionLog {
    private static Log logger = LogFactory.getLog("TaskExecutionLog");

    private static final String LogLevel_FATAL = "FATAL";
    private static final String LogLevel_ERROR = "ERROR";
    private static final String LogLevel_WARN = "WARN";
    private static final String LogLevel_INFO = "INFO";
    private static final String LogLevel_DEBUG = "DEBUG";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private JobExecutionContext jobExecutionContext;
    private StringBuilder logContent;

    public TaskExecutionLog(JobExecutionContext context) {
        Map<String, Object> contextResultMap = JobExecutionContextUtil.getContextResultMap(context);

        StringBuilder logContent = (StringBuilder) contextResultMap.get(Constants.JOB_EXEC_CONTEXT_RESULT_MAP_KEY_TASK_LOG);
        if (logContent == null) {
            logContent = new StringBuilder();
            contextResultMap.put(Constants.JOB_EXEC_CONTEXT_RESULT_MAP_KEY_TASK_LOG, logContent);
        }
        this.logContent = logContent;
    }

    public void debug(String msg) {
        log(LogLevel_DEBUG, msg, null);
    }

    public void debug(String msg, Throwable e) {
        log(LogLevel_DEBUG, msg, e);
    }

    public void info(String msg) {
        log(LogLevel_INFO, msg, null);
    }

    public void info(String msg, Throwable e) {
        log(LogLevel_INFO, msg, e);
    }

    public void warning(String msg) {
        log(LogLevel_WARN, msg, null);
    }

    public void warning(String msg, Throwable e) {
        log(LogLevel_WARN, msg, e);
    }

    public void error(String msg) {
        log(LogLevel_ERROR, msg, null);
    }

    public void error(String msg, Throwable e) {
        log(LogLevel_ERROR, msg, e);
    }

    public void fatal(String msg) {
        log(LogLevel_FATAL, msg, null);
    }

    public void fatal(String msg, Throwable e) {
        log(LogLevel_FATAL, msg, e);
    }

    private void log(String level, String msg, Throwable e) {
        if (LogLevel_DEBUG.equals(level)) {
            logger.debug(msg, e);
        } else if (LogLevel_INFO.equals(level)) {
            logger.info(msg, e);
        } else if (LogLevel_WARN.equals(level)) {
            logger.warn(msg, e);
        } else if (LogLevel_ERROR.equals(level)) {
            logger.error(msg, e);
        } else if (LogLevel_FATAL.equals(level)) {
            logger.fatal(msg, e);
        }
        appendLogLine(level, msg);
        if (e != null) {
            appendLogLine(level, "StackTrace:");
            StackTraceElement[] stackArray = e.getStackTrace();
            for (StackTraceElement stackTraceElement : stackArray) {
                appendLogLine(level, "at " + stackTraceElement.toString());
            }
            Throwable cause = e.getCause();
            if (cause != null) {
                appendLogLine(level, "Cause:" + cause.getMessage());
                for (StackTraceElement stackTraceElement : cause.getStackTrace()) {
                    appendLogLine(level, "at " + stackTraceElement.toString());
                }
            }
        }
    }

    private void appendLogLine(String level, String msg) {
        logContent.append(level).append(" [").append(sdf.format(new Date())).append("] : ").append(msg).append("\r");
    }

    public String getLogContent() {
        return logContent.toString();
    }


}
