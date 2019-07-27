package com.bee.scheduler.daemonnode.web;

import com.bee.scheduler.daemonnode.model.UserLoginSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    protected Log logger = LogFactory.getLog(getClass());

    public UserLoginSession getUserSession(HttpServletRequest request) {
        return null;
    }
}
