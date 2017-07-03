package com.bee.scheduler;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

public class SimpleTest {
    private static final String PATH = "/example/queue";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = null;
        DistributedQueue<String> queue = null;
        try {
            client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(1000, 3));
            client.getCuratorListenable().addListener(new CuratorListener() {
                @Override
                public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                    System.out.println("CuratorEvent: " + event.getType().name());
                }
            });


            client.start();

            String directory = "/bee-scheduler/1.0/nodes";


            System.out.println("111111111");

            System.out.println("================" + client.checkExists().forPath(directory));

        } catch (Exception ex) {

        } finally {
            CloseableUtils.closeQuietly(queue);
            CloseableUtils.closeQuietly(client);
        }
    }
}
