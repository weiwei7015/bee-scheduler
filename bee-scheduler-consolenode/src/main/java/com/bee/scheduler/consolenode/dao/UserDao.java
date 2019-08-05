package com.bee.scheduler.consolenode.dao;

import com.bee.scheduler.consolenode.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends AbstractDao {
    public User getByAccount$Pwd(String account, String pwd) {
        try {
            return jdbcTemplate.queryForObject("select * from BS_USER where ACCOUNT = ? and PWD = ?", new BeanPropertyRowMapper<>(User.class), account, pwd);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}