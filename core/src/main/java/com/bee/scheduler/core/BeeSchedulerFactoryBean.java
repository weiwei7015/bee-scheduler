package com.bee.scheduler.core;

import com.bee.scheduler.core.listener.TaskEventRecorder;
import com.bee.scheduler.core.listener.TaskLinkageHandleListener;
import com.bee.scheduler.core.listener.TaskListenerSupport;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author weiwei.
 */
public class BeeSchedulerFactoryBean extends SchedulerFactoryBean {
    private String instanceId;
    private boolean autoGenerateInstanceId = true;
    private int threadPoolSize = 10;
    private boolean clusterMode = false;
    private long clusterCheckinInterval = 5000;
    private long misfireThreshold = 5000L; // 5 second
    private DataSource dataSource;
    private List<TaskListenerSupport> taskListenerList = new ArrayList<>();


    public BeeSchedulerFactoryBean(String name, String instanceId, DataSource dataSource) {
        this.instanceId = instanceId;
        this.dataSource = dataSource;
        this.setSchedulerName(name);
        this.setDataSource(dataSource);
    }

    public BeeSchedulerFactoryBean(String name, DataSource dataSource) {
        this(name, null, dataSource);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Properties quartzProperties = new Properties();
        quartzProperties.setProperty("org.quartz.jobStore.tablePrefix", "BS_");
        quartzProperties.setProperty("org.quartz.jobStore.useProperties", "true");
        quartzProperties.setProperty("org.quartz.jobStore.misfireThreshold", String.valueOf(misfireThreshold));
        quartzProperties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        if (autoGenerateInstanceId || instanceId == null) {
            quartzProperties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        } else {
            quartzProperties.setProperty("org.quartz.scheduler.instanceId", instanceId);
        }
        if (clusterMode) {
            quartzProperties.setProperty("org.quartz.jobStore.isClustered", "true");
            quartzProperties.setProperty("org.quartz.jobStore.clusterCheckinInterval", String.valueOf(clusterCheckinInterval));
        }
        quartzProperties.setProperty("org.quartz.threadPool.threadCount", String.valueOf(threadPoolSize));
        this.setQuartzProperties(quartzProperties);

        super.afterPropertiesSet();
    }

    public void addListener(TaskListenerSupport listener) {
        this.taskListenerList.add(listener);
    }

    @Override
    protected Scheduler createScheduler(SchedulerFactory schedulerFactory, String schedulerName) throws SchedulerException {
        Scheduler scheduler = super.createScheduler(schedulerFactory, schedulerName);

        taskListenerList.add(new TaskLinkageHandleListener());
        taskListenerList.add(new TaskEventRecorder(dataSource));

        ListenerManager listenerManager = scheduler.getListenerManager();

        for (TaskListenerSupport listener : taskListenerList) {
            listenerManager.addJobListener(listener);
            listenerManager.addTriggerListener(listener);
        }

        return scheduler;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public boolean isAutoGenerateInstanceId() {
        return autoGenerateInstanceId;
    }

    public void setAutoGenerateInstanceId(boolean autoGenerateInstanceId) {
        this.autoGenerateInstanceId = autoGenerateInstanceId;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public boolean isClusterMode() {
        return clusterMode;
    }

    public void setClusterMode(boolean clusterMode) {
        this.clusterMode = clusterMode;
    }

    public long getClusterCheckinInterval() {
        return clusterCheckinInterval;
    }

    public void setClusterCheckinInterval(long clusterCheckinInterval) {
        this.clusterCheckinInterval = clusterCheckinInterval;
    }
}
