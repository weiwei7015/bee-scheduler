package com.bee.scheduler.core;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by weiwei.
 */
public class BeeSchedulerFactoryBean extends SchedulerFactoryBean {
    private String instanceName = "ClusteredBeeScheduler-Node1";
    private boolean autoGenerateInstanceId = true;
    private int threadPoolSize = 10;
    private boolean clusterMode = false;
    private long clusterCheckinInterval = 5000;


    public BeeSchedulerFactoryBean(String name, DataSource dataSource) {
        this.setDataSource(dataSource);
        this.setSchedulerName(name);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Properties quartzProperties = new Properties();
        quartzProperties.setProperty("org.quartz.jobStore.useProperties", "true");
        quartzProperties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        quartzProperties.setProperty("org.quartz.scheduler.instanceName", instanceName);
        if (autoGenerateInstanceId) {
            quartzProperties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        }
        if (clusterMode) {
            quartzProperties.setProperty("org.quartz.jobStore.isClustered", "true");
            quartzProperties.setProperty("org.quartz.jobStore.clusterCheckinInterval", String.valueOf(clusterCheckinInterval));
        }
        quartzProperties.setProperty("org.quartz.threadPool.threadCount", String.valueOf(threadPoolSize));
        this.setQuartzProperties(quartzProperties);

        super.afterPropertiesSet();
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
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
