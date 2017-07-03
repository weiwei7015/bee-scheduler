package com.bee.scheduler.core;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Created by wei-wei
 */
public class CuratorFrameworkFactoryBean extends AbstractFactoryBean<CuratorFramework> {

    private String connectString;
    private RetryPolicy retryPolicy;

    public CuratorFrameworkFactoryBean(String connectString, RetryPolicy retryPolicy) {
        this.connectString = connectString;
        this.retryPolicy = retryPolicy;
    }

    @Override
    public Class<?> getObjectType() {
        return CuratorFramework.class;
    }

    @Override
    protected CuratorFramework createInstance() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        client.start();
        return client;
    }

    @Override
    protected void destroyInstance(CuratorFramework instance) throws Exception {
        CloseableUtils.closeQuietly(instance);
        super.destroyInstance(instance);
    }
}
