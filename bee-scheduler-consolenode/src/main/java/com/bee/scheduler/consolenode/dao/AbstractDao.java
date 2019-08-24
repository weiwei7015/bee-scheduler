package com.bee.scheduler.consolenode.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * @author weiwei
 */
class AbstractDao {
    static final int DEFAULT_PAGE_SIZE = 20;
    Log logger = LogFactory.getLog(getClass());
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
}
