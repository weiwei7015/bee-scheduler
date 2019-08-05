package com.bee.scheduler.consolenode.dao;

import com.bee.scheduler.consolenode.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class UserDao extends AbstractDao {
    public User getByAccount$Pwd(String account, String pwd) {
        try {
            return jdbcTemplate.queryForObject("select * from BS_USER where ACCOUNT = ? and PWD = ?", new BeanPropertyRowMapper<>(User.class), account, pwd);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updateByPrimary(User entity) {
        ArrayList<String> setters = new ArrayList<>();
        if (entity.getPwd() != null) {
            setters.add("PWD = :pwd");
        }
        if (entity.getName() != null) {
            setters.add("NAME = :name");
        }
        String sql = "update BS_USER set " + String.join(",", setters) + " where ACCOUNT = :account";
        namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(entity));
    }
}