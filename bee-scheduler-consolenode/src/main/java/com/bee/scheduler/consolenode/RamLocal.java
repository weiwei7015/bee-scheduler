//package com.bee.scheduler.consolenode;
//
//
//import com.bee.scheduler.consolenode.model.Notification;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// * @author weiwei
// */
//public class RamLocal {
//    public static List<Notification> notifications = Collections.synchronizedList(new ArrayList<Notification>());
//
//    public static synchronized void addNotification(Notification n) {
//        int maxSize = 5;
//        if (notifications.size() == maxSize) {
//            notifications.remove(0);
//        }
//        notifications.add(n);
//    }
//}
