package com.bee.scheduler.admin.web;

import com.bee.scheduler.admin.core.RamLocal;
import com.bee.scheduler.admin.model.Notification;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author weiwei
 */
@Controller
public class NotifyController {
    @ResponseBody
    @RequestMapping(value = {"/notices"}, method = {RequestMethod.GET})
    public Object listener(HttpServletRequest request, HttpServletResponse response, long offset) throws Exception {
        Date date = new Date(offset);
        //拉取通知信息
        List<Notification> notificationList = new ArrayList<>();
        long startTime = (new Date()).getTime();
        int timeout = 1000 * 60;
        while (((new Date()).getTime() - startTime) < timeout && CollectionUtils.isEmpty(notificationList = fetchNotices(new Date(offset), RamLocal.notifications))) {
            Thread.sleep(100);
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("notificationList", notificationList);
        result.put("offset", new Date().getTime());
        return result;
    }

    private List<Notification> fetchNotices(Date date, List<Notification> notificationList) {
        List<Notification> fetchResult = new ArrayList<>();
        for (int i = 0; i < RamLocal.notifications.size(); i++) {
            Notification notification = RamLocal.notifications.get(i);
            if (date.compareTo(notification.getPublishTime()) == -1) {
                fetchResult = RamLocal.notifications.subList(i, RamLocal.notifications.size());
                break;
            }
        }
        return fetchResult;
    }
}
