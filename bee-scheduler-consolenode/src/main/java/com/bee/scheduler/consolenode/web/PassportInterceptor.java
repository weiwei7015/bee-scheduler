package com.bee.scheduler.consolenode.web;

import com.bee.scheduler.consolenode.exception.UnauthorizedException;
import com.bee.scheduler.consolenode.model.UserPassport;
import com.bee.scheduler.consolenode.util.AESUtil;
import com.bee.scheduler.consolenode.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author weiwei
 */

public class PassportInterceptor extends HandlerInterceptorAdapter {
    private Log logger = LogFactory.getLog(getClass());
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("PassportInterceptor.preHandle");

        Cookie passport = WebUtils.getCookie(request, Constants.COOKIE_NAME_PASSPORT);
        if (passport == null) {
            throw new UnauthorizedException();
        }
        try {
            UserPassport userPassport = objectMapper.readValue(AESUtil.decrypt(passport.getValue()), UserPassport.class);
            request.setAttribute(Constants.REQUEST_ATTR_KEY_USER_PASSPORT, userPassport);
        } catch (Exception e) {
            logger.error("passport解析失败", e);
            throw new UnauthorizedException();
        }

        return true;
    }
}