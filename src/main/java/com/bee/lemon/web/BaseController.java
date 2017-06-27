package com.bee.lemon.web;

import com.bee.lemon.model.UserLoginSession;
import com.bee.lemon.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    protected Log logger = LogFactory.getLog(getClass());

    public UserLoginSession getUserSession(HttpServletRequest request) {
        return (UserLoginSession) request.getAttribute(Constants.USER_SESSION_KEY);
    }
}
