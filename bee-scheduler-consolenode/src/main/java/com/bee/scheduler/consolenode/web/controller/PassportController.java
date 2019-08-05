package com.bee.scheduler.consolenode.web.controller;

import com.bee.scheduler.consolenode.entity.User;
import com.bee.scheduler.consolenode.exception.UnauthorizedException;
import com.bee.scheduler.consolenode.model.UserPassport;
import com.bee.scheduler.consolenode.service.UserService;
import com.bee.scheduler.consolenode.util.AESUtil;
import com.bee.scheduler.consolenode.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author weiwei
 */
@RestController
@RequestMapping("/passport")
public class PassportController extends AbstractController {
    private Log logger = LogFactory.getLog(getClass());
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserService userService;

    @GetMapping("/status")
    public ResponseEntity status(HttpServletRequest request) {
        UserPassport passport = null;
        try {
            Cookie ppCookie = WebUtils.getCookie(request, Constants.COOKIE_NAME_PASSPORT);
            passport = objectMapper.readValue(AESUtil.decrypt(ppCookie.getValue()), UserPassport.class);
        } catch (Exception e) {
            //ignore...
        }
        return ResponseEntity.ok(passport != null);
    }

    @GetMapping("/info")
    public ResponseEntity info(HttpServletRequest request) {
        UserPassport passport = null;
        try {
            Cookie ppCookie = WebUtils.getCookie(request, Constants.COOKIE_NAME_PASSPORT);
            passport = objectMapper.readValue(AESUtil.decrypt(ppCookie.getValue()), UserPassport.class);
        } catch (Exception e) {
            throw new UnauthorizedException();
        }
        return ResponseEntity.ok(passport);
    }

    @PostMapping("/login")
    public ResponseEntity login(String account, String password, HttpServletResponse response) {
        User user = userService.getByAccount$Pwd(account, password);
        if (user == null) {
            return ResponseEntity.ok(0);
        }

        UserPassport userPassport = new UserPassport();
        userPassport.setAccount(user.getAccount());
        userPassport.setName(user.getName());

        try {
            Cookie ppCookie = new Cookie(Constants.COOKIE_NAME_PASSPORT, AESUtil.encrypt(objectMapper.writeValueAsString(userPassport)));
            ppCookie.setPath("/");
            ppCookie.setMaxAge(3600 * 24 * 7);
            response.addCookie(ppCookie);
        } catch (Exception e) {
            logger.error("写入passport cookie失败", e);
            return ResponseEntity.ok(0);
        }

        return ResponseEntity.ok(1);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie ppCookie = new Cookie(Constants.COOKIE_NAME_PASSPORT, "");
        ppCookie.setPath("/");
        ppCookie.setMaxAge(0);
        response.addCookie(ppCookie);
        return ResponseEntity.ok("success");
    }
}