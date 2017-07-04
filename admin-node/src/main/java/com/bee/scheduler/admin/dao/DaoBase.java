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

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


    protected Object getObjectFromBlob(ResultSet rs, String colName) throws ClassNotFoundException, IOException, SQLException {
        Object obj = null;

        Blob blobLocator = rs.getBlob(colName);
        if (blobLocator != null && blobLocator.length() != 0) {
            InputStream binaryInput = blobLocator.getBinaryStream();

            if (null != binaryInput) {
                if (binaryInput instanceof ByteArrayInputStream
                        && ((ByteArrayInputStream) binaryInput).available() == 0) {
                    //do nothing
                } else {
                    ObjectInputStream in = new ObjectInputStream(binaryInput);
                    try {
                        obj = in.readObject();
                    } finally {
                        in.close();
                    }
                }
            }

        }
        return obj;
    }

    private Map<?, ?> getMapFromProperties(ResultSet rs, String colName)
            throws ClassNotFoundException, IOException, SQLException {
        Map<?, ?> map;
        InputStream is = (InputStream) getJobDataFromBlob(rs, colName);
        if(is == null) {
            return null;
        }
        Properties properties = new Properties();
        if (is != null) {
            try {
                properties.load(is);
            } finally {
                is.close();
            }
        }
        map = new HashMap<Object, Object>(properties);
        return map;
    }

    protected Object getJobDataFromBlob(ResultSet rs, String colName)
            throws ClassNotFoundException, IOException, SQLException {
        if (true) {
            Blob blobLocator = rs.getBlob(colName);
            if (blobLocator != null) {
                InputStream binaryInput = blobLocator.getBinaryStream();
                return binaryInput;
            } else {
                return null;
            }
        }

        return getObjectFromBlob(rs, colName);
    }


}
