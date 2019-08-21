package com.bee.scheduler.consolenode.service.impl;

import com.bee.scheduler.consolenode.dao.DaoSupport;
import com.bee.scheduler.consolenode.entity.User;
import com.bee.scheduler.consolenode.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author weiwei
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private DaoSupport dao;

    @Override
    public User getByAccount$Pwd(String account, String pwd) {
        return dao.getUserByAccount$Pwd(account, pwd);
    }

    @Override
    public void updatePwdByAccount(String account, String pwd) {
        User user = new User();
        user.setAccount(account);
        user.setPwd(pwd);
        dao.updateUserByAccount(user);
    }
}