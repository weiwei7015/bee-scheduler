package com.bee.scheduler.daemonnode.core;


import com.bee.scheduler.daemonnode.model.Notification;
import com.bee.scheduler.context.job.AbstractJobComponent;

import java.util.*;

/**
 * @author weiwei 内存存储容器
 */
public class RamLocal {
    // Job Map<Name，JobComponent>
    public static Map<String, AbstractJobComponent> JobComponentMap = new HashMap<>();
    // 通知
    public static List<Notification> notifications = Collections.synchronizedList(new ArrayList<Notification>());

    public static synchronized void addNotification(Notification n) {
        int maxSize = 5;
        if (notifications.size() == maxSize) {
            notifications.remove(0);
        }
        notifications.add(n);
    }
}
