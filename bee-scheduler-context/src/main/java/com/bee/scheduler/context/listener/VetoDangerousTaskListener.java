package com.bee.scheduler.context.listener;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;

public class VetoDangerousTaskListener extends TaskListenerSupport {
    @Override
    public String getName() {
        return "VetoDangerousTaskListener";
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        int minExecInterval = 3000;
        if (context.getPreviousFireTime() != null && context.getFireTime().getTime() - context.getPreviousFireTime().getTime() <= minExecInterval) {
            logger.warn("任务最近执行时间：" + DateFormatUtils.format(context.getPreviousFireTime(), "yyyy-MM-dd HH:mm:ss") + "，任务执行间隔不能低于" + minExecInterval + "ms，请调整任务配置");
            return true;
        }
        return false;
    }
}
