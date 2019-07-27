package com.bee.scheduler.consolenode.core;


import com.bee.scheduler.consolenode.model.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author weiwei 内存存储容器
 */
public class RamLocal {
    // Job Map<Name，JobComponent>
//    public static Map<String, AbstractJobComponent> JobComponentMap = new HashMap<>();
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
