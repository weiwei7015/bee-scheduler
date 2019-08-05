package com.bee.scheduler.consolenode.service.impl;

import com.bee.scheduler.consolenode.dao.UserDao;
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
    private UserDao userDao;


    @Override
    public User getByAccount$Pwd(String account, String pwd) {
        return userDao.getByAccount$Pwd(account, pwd);
    }

    @Override
    public void updatePwdByAccount(String account, String pwd) {
        User user = new User();
        user.setAccount(account);
        user.setPwd(pwd);
        userDao.updateByPrimary(user);
    }
}