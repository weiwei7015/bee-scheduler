package com.bee.scheduler.consolenode.web;

import com.bee.scheduler.consolenode.model.UserLoginSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    protected Log logger = LogFactory.getLog(getClass());

    public UserLoginSession getUserSession(HttpServletRequest request) {
        return null;
    }
}
