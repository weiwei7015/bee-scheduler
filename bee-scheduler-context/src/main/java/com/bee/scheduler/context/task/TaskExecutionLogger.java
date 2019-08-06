package com.bee.scheduler.context.task;

/**
 * @author weiwei
 */
public class TaskExecutionLogger {
    private StringBuilder logContent = new StringBuilder();
//    private static final String LOG_LEVEL_DEBUG = "DEBUG";
//    private static final String LOG_LEVEL_INFO = "INFO";
//    private static final String LOG_LEVEL_WARN = "WARN";
//    private static final String LOG_LEVEL_ERROR = "ERROR";
//    private static final String LOG_LEVEL_FATAL = "FATAL";

//    public void debug(String msg) {
//        log(LOG_LEVEL_DEBUG, msg, null);
//    }
//
//    public void debug(String msg, Throwable e) {
//        log(LOG_LEVEL_DEBUG, msg, e);
//    }
//
//    public void info(String msg) {
//        log(LOG_LEVEL_INFO, msg, null);
//    }
//
//    public void info(String msg, Throwable e) {
//        log(LOG_LEVEL_INFO, msg, e);
//    }
//
//    public void warning(String msg) {
//        log(LOG_LEVEL_WARN, msg, null);
//    }
//
//    public void warning(String msg, Throwable e) {
//        log(LOG_LEVEL_WARN, msg, e);
//    }
//
//    public void error(String msg) {
//        log(LOG_LEVEL_ERROR, msg, null);
//    }
//
//    public void error(String msg, Throwable e) {
//        log(LOG_LEVEL_ERROR, msg, e);
//    }
//
//    public void fatal(String msg) {
//        log(LOG_LEVEL_FATAL, msg, null);
//    }
//
//    public void fatal(String msg, Throwable e) {
//        log(LOG_LEVEL_FATAL, msg, e);
//    }
//
//    private void log(String level, String msg, Throwable e) {
//        appendLog(level, msg);
//        if (e != null) {
//            appendLog(level, "StackTrace:");
//            StackTraceElement[] stackArray = e.getStackTrace();
//            for (StackTraceElement stackTraceElement : stackArray) {
//                appendLog(level, "at " + stackTraceElement.toString());
//            }
//            Throwable cause = e.getCause();
//            if (cause != null) {
//                appendLog(level, "Cause:" + cause.getMessage());
//                for (StackTraceElement stackTraceElement : cause.getStackTrace()) {
//                    appendLog(level, "at " + stackTraceElement.toString());
//                }
//            }
//        }
//    }
//
//    private void appendLog(String msg) {
//        appendLog(null, msg);
//    }
//
//    private void appendLog(String level, String msg) {
//        SimpleDateFormat dateFormatter = new SimpleDateFormat();
//        if (level != null) {
//            logContent.append("[").append(level).append("]").append(" [").append(dateFormatter.format(new Date())).append("] : ").append(msg).append("\n");
//        } else {
//            logContent.append(msg).append("\n");
//        }
//    }

    public String getLog() {
        return logContent.toString();
    }


    public void appendLine(String msg) {
        logContent.append(msg).append("\n");
    }
}
