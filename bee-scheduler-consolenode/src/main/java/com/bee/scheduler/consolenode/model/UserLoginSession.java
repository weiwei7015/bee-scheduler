package com.bee.scheduler.consolenode.model;

import java.io.Serializable;
import java.util.Date;

public class UserLoginSession implements Serializable {
    private static final long serialVersionUID = -8172879662124856674L;

    private String name;

    private String account;

    private Date loginTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}
