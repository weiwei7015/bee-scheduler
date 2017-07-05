package com.bee.scheduler.admin.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author weiwei
 */
public class DaoBase {
    protected Log logger = LogFactory.getLog(getClass());

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected int pageSize = 20;
}
