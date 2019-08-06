package com.bee.scheduler.context.listener;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author weiwei
 */
public abstract class TaskListenerSupport implements JobListener, TriggerListener, ApplicationContextAware {
    protected ApplicationContext applicationContext;
    protected final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {

    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {

    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {

    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {

    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {

    }

    protected String getTaskLog() {
        return ThreadLocalTaskLoggerAppender.getLogContent();
    }
}
