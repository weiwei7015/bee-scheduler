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
        int minExecInterval = 1000 * 30;//执行频率限制为30秒
        //因系统设置的batchTriggerAcquisitionFireAheadTimeWindow=5000,此处检查执行频率时允许有5秒的误差
        int broadMinExecInterval = minExecInterval - 5000;
        if (context.getPreviousFireTime() != null && context.getFireTime().getTime() - context.getPreviousFireTime().getTime() < broadMinExecInterval) {
            logger.warn("任务上次执行时间：" + DateFormatUtils.format(context.getPreviousFireTime(), "yyyy-MM-dd HH:mm:ss") + "，任务频率不能高于" + minExecInterval + "ms/次，请调整任务配置");
            return true;
        }
        return false;
    }
}
