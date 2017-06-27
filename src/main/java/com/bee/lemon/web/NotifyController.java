package com.bee.lemon.web;

import com.bee.lemon.core.RamStore;
import com.bee.lemon.model.Notification;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        List<Notification> notificationList = Lists.newArrayList();
        long startTime = (new Date()).getTime();
        int timeout = 1000 * 60;
        while (((new Date()).getTime() - startTime) < timeout && CollectionUtils.isEmpty(notificationList = fetchNotices(new Date(offset), RamStore.notifications))) {
            Thread.sleep(100);
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("notificationList", notificationList);
        result.put("offset", new Date().getTime());
        return result;
    }

    private List<Notification> fetchNotices(Date date, List<Notification> notificationList) {
        List<Notification> fetchResult = Lists.newArrayList();
        for (int i = 0; i < RamStore.notifications.size(); i++) {
            Notification notification = RamStore.notifications.get(i);
            if (date.compareTo(notification.getPublishTime()) == -1) {
                fetchResult = RamStore.notifications.subList(i, RamStore.notifications.size());
                break;
            }
        }
        return fetchResult;
    }
}
