package com.bee.lemon.core;


import com.bee.lemon.core.job.JobComponent;
import com.bee.lemon.model.Notification;

import java.util.*;

/**
 * @author weiwei 内存存储容器
 */
public class RamStore {
    // Job Map<jobClass，JobDefinition>
    public static Map<String, JobComponent> jobs = new HashMap<String, JobComponent>();
    // 通知
    public static List<Notification> notifications = Collections.synchronizedList(new ArrayList<>());

    public static synchronized void addNotification(Notification n) {
        int maxSize = 5;
        if (notifications.size() == maxSize) {
            notifications.remove(0);
        }
        notifications.add(n);
    }
}
