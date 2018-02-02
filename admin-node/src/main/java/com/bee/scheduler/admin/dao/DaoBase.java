package com.bee.scheduler.admin.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author weiwei
 */
public class DaoBase {
    protected Log logger = LogFactory.getLog(getClass());

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected int pageSize = 20;
}
