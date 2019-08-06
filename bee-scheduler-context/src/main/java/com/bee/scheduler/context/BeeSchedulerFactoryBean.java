package com.bee.scheduler.context;

import com.bee.scheduler.context.task.TaskScheduler;
import org.quartz.Scheduler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @author weiwei.
 */
public class BeeSchedulerFactoryBean implements FactoryBean<TaskScheduler>, InitializingBean {
    private TaskScheduler scheduler;
    private SchedulerFactoryBean schedulerFactoryBean;

    public BeeSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    @Override
    public TaskScheduler getObject() {
        return this.scheduler;
    }

    public Class<?> getObjectType() {
        return (this.scheduler != null ? this.scheduler.getClass() : Scheduler.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.scheduler = new TaskScheduler(schedulerFactoryBean.getObject());
    }
}
