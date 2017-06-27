package com.bee.lemon.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

public class HsqlFileEmbeddedDatabaseConfigurer implements EmbeddedDatabaseConfigurer {

	protected final Log logger = LogFactory.getLog(getClass());

	private static HsqlFileEmbeddedDatabaseConfigurer instance;

	private final Class<? extends Driver> driverClass;

	/**
	 * Get the singleton {@link HsqlFileEmbeddedDatabaseConfigurer} instance.
	 * 
	 * @return the configurer
	 * @throws ClassNotFoundException
	 *             if HSQL is not on the classpath
	 */
	@SuppressWarnings("unchecked")
	public static synchronized HsqlFileEmbeddedDatabaseConfigurer getInstance() throws ClassNotFoundException {
		if (instance == null) {
			Class<? extends Driver> clazz = (Class<? extends Driver>) ClassUtils.forName("org.hsqldb.jdbcDriver", HsqlFileEmbeddedDatabaseConfigurer.class.getClassLoader());
			instance = new HsqlFileEmbeddedDatabaseConfigurer(clazz);
		}
		return instance;
	}

	private HsqlFileEmbeddedDatabaseConfigurer(Class<? extends Driver> driverClass) {
		this.driverClass = driverClass;
	}

	@Override
	public void configureConnectionProperties(ConnectionProperties properties, String databaseName) {
		properties.setDriverClass(this.driverClass);
		properties.setUrl("jdbc:hsqldb:file:" + databaseName);
		properties.setUsername("sa");
		properties.setPassword("");
	}

	@Override
	public void shutdown(DataSource dataSource, String databaseName) {
		Connection con = null;
		try {
			con = dataSource.getConnection();
			con.createStatement().execute("SHUTDOWN");
		} catch (SQLException ex) {
			logger.warn("Could not shut down embedded database", ex);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Throwable ex) {
					logger.debug("Could not close JDBC Connection on shutdown", ex);
				}
			}
		}
	}

}