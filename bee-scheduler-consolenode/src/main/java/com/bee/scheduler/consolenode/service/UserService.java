package com.bee.scheduler.consolenode.service;


import com.bee.scheduler.consolenode.entity.User;

/**
 * @author weiwei
 */
public interface UserService {
    User getByAccount$Pwd(String account, String pwd);
}
