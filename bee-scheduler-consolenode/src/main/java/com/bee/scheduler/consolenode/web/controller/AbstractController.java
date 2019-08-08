package com.bee.scheduler.consolenode.web.controller;

import com.bee.scheduler.consolenode.model.UserPassport;
import com.bee.scheduler.consolenode.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;

public class AbstractController {
    protected Log logger = LogFactory.getLog(getClass());

    public UserPassport getUserPassport(HttpServletRequest request) {
        return (UserPassport) request.getAttribute(Constants.REQUEST_ATTR_KEY_USER_PASSPORT);
    }
}
