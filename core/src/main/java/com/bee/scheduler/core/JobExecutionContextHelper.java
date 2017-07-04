package com.bee.scheduler.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author weiwei Job执行上下文辅助类
 */
@SuppressWarnings("unchecked")
public class JobExecutionContextHelper {
    private static Log log = LogFactory.getLog("JobExecutionLog");

    private static final String TASK_EXEC_LOG_KEY = "execLog";

    public static final String LogLevel_FATAL = "FATAL";
    public static final String LogLevel_ERROR = "ERROR";
    public static final String LogLevel_WARN = "WARN";
    public static final String LogLevel_INFO = "INFO";
    public static final String LogLevel_DEBUG = "DEBUG";

    /**
     * 获取任务日志
     */
    public static String getExecLog(JobExecutionContext context) {
        StringBuffer execLogMessage = (StringBuffer) getContextResult(context).get(TASK_EXEC_LOG_KEY);
        return execLogMessage.toString();
    }

    /**
     * 追加任务日志
     */
    public static void appendExecLog(JobExecutionContext context, String loginfo) {
        JobExecutionContextHelper.appendExecLog(context, loginfo, LogLevel_INFO);
    }

    /**
     * 追加任务日志
     */
    public static void appendExecLog(JobExecutionContext context, String message, String level) {
        String dateFormatPattern = "yyyy-MM-dd HH:mm:ss";
        Date currentTime = Calendar.getInstance().getTime();
        String formatedCurrentTime = new SimpleDateFormat(dateFormatPattern).format(currentTime);


        if (LogLevel_DEBUG.equals(level)) {
            log.debug(message);
        } else if (LogLevel_INFO.equals(level)) {
            log.info(message);
        } else if (LogLevel_WARN.equals(level)) {
            log.warn(message);
        } else if (LogLevel_ERROR.equals(level)) {
            log.error(message);
        } else if (LogLevel_FATAL.equals(level)) {
            log.fatal(message);
        }
        Map<String, Object> contextResult = getContextResult(context);
        StringBuffer execLog = (StringBuffer) contextResult.get(TASK_EXEC_LOG_KEY);
        if (execLog == null) {
            execLog = new StringBuffer();
            contextResult.put(TASK_EXEC_LOG_KEY, execLog);
        }
        execLog.append(level).append("[").append(formatedCurrentTime).append("] : ").append(message).append("\r");
    }

    public static Map<String, Object> getContextResult(JobExecutionContext context) {
        Map<String, Object> result = (Map<String, Object>) context.getResult();
        if (result == null) {
            result = new HashMap<>();
            context.setResult(result);
        }
        return result;
    }

}
