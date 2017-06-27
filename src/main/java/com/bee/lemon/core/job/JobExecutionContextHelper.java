package com.bee.lemon.core.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.lf5.LogLevel;
import org.quartz.JobExecutionContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author weiwei1
 *         <p>
 *         Job执行上下文辅助类
 */
@SuppressWarnings("unchecked")
public class JobExecutionContextHelper {
    private static Log log = LogFactory.getLog("JobExecutionLog");

    private static final String TASK_EXEC_LOG_KEY = "execLog";

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
        JobExecutionContextHelper.appendExecLog(context, loginfo, LogLevel.INFO);
    }

    /**
     * 追加任务日志
     */
    public static void appendExecLog(JobExecutionContext context, String message, LogLevel level) {
        String dateFormatPattern = "yyyy-MM-dd HH:mm:ss";
        Date currentTime = Calendar.getInstance().getTime();
        String formatedCurrentTime = new SimpleDateFormat(dateFormatPattern).format(currentTime);


        if (level == LogLevel.DEBUG) {
            log.debug(message);
        } else if (level == LogLevel.INFO) {
            log.info(message);
        } else if (level == LogLevel.WARN) {
            log.warn(message);
        } else if (level == LogLevel.ERROR) {
            log.error(message);
        } else if (level == LogLevel.FATAL) {
            log.fatal(message);
        }
        Map<String, Object> contextResult = getContextResult(context);
        StringBuffer execLog = (StringBuffer) contextResult.get(TASK_EXEC_LOG_KEY);
        if (execLog == null) {
            execLog = new StringBuffer();
            contextResult.put(TASK_EXEC_LOG_KEY, execLog);
        }
        execLog.append(level.toString() + "[" + formatedCurrentTime + "] : " + message + "\r");
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
