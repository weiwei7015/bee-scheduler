package com.bee.scheduler.consolenode.web.controller;

import com.bee.scheduler.consolenode.entity.User;
import com.bee.scheduler.consolenode.exception.BadRequestException;
import com.bee.scheduler.consolenode.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weiwei
 */
@RestController
public class UserController extends AbstractController {
    private Log logger = LogFactory.getLog(UserController.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserService userService;

    @PostMapping("/user/updatepassword")
    public ResponseEntity updatePassword(String account, String oldpassword, String newpassword, String renewpassword) throws Exception {
        User user = userService.getByAccount$Pwd(account, oldpassword);
        if (user == null) {
            throw new BadRequestException("原始密码有误");
        }

        if (!StringUtils.equals(newpassword, renewpassword)) {
            throw new BadRequestException("确认密码不一致");
        }
        userService.updatePwdByAccount(user.getAccount(), newpassword);
        return ResponseEntity.ok("success");
    }
}