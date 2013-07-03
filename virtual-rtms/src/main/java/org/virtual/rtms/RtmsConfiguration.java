package org.virtual.rtms;

import static org.virtual.rtms.Constants.CONFIG_DRIVER;
import static org.virtual.rtms.Constants.CONFIG_ENDPOINT;
import static org.virtual.rtms.Constants.CONFIG_NO_REFRESH;
import static org.virtual.rtms.Constants.CONFIG_PWD;
import static org.virtual.rtms.Constants.CONFIG_URL;
import static org.virtual.rtms.Constants.CONFIG_USER;
import static org.virtualrepository.Utils.notNull;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

import javax.sql.DataSource;
import javax.sql.PooledConnection;

import oracle.jdbc.pool.OraclePooledConnection;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.metadata.RtmsMetadata;
import org.virtual.rtms.metadata.RtmsMetadataBrowser;

public class RtmsConfiguration {

	private static Logger log = LoggerFactory.getLogger(RtmsConfiguration.class);
	
	public static final String ORACLE_JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	
	/* FFiorellato - removed */
	//private final PooledConnection connectionsPool;
	private final Properties properties;
	private RtmsMetadata metadata;
	private DataSource dataSource;
	
	public RtmsConfiguration(Properties properties) {
		
		this.properties=properties;
		
		validateStaticConfiguration(properties);
		
		/* FFiorellato - removed */
		//init pool
		//this.connectionsPool = newConnectionPool();
		
		this.dataSource = this.setupDataSource();
	}
	
	public Connection connection() {
		
		try {
//			return connectionsPool.getConnection();
			
			return this.dataSource.getConnection();
		}
		catch(Exception e) {
			throw new RuntimeException("cannot connect to RTMS (see cause)",e);
		}
	}
	
	public String url(){
		return properties.getProperty(CONFIG_URL);
	}
	
	public RtmsMetadata metadata() {
		return metadata;
	}
	
	public void refresh() {
		
		//refresh first time or if we are told to do it all the time
		if (metadata==null || !properties.containsKey(CONFIG_NO_REFRESH)) {
			RtmsMetadataBrowser browser = new RtmsMetadataBrowser(this);
			metadata = browser.discover();
		}
	}
	
	//helpers
	
	private void validateStaticConfiguration(Properties properties) {
		
		notNull("configuration", properties);
		
		notNull(CONFIG_ENDPOINT, properties.getProperty(CONFIG_ENDPOINT));
		notNull(CONFIG_DRIVER, properties.getProperty(CONFIG_DRIVER));
		notNull(CONFIG_URL, properties.getProperty(CONFIG_URL));
		notNull(CONFIG_USER, properties.getProperty(CONFIG_USER));
		notNull(CONFIG_PWD, properties.getProperty(CONFIG_PWD));
	}
	
	public DataSource setupDataSource() {
		try {
			BasicDataSource ds = new BasicDataSource();
			ds.setDriverClassName(properties.getProperty(ORACLE_JDBC_DRIVER));
			ds.setUrl(properties.getProperty(CONFIG_ENDPOINT));
			ds.setUsername(properties.getProperty(CONFIG_USER));
			ds.setPassword(properties.getProperty(CONFIG_PWD));
			ds.setDefaultAutoCommit(false);
			
			ds.setMaxActive(50);
			ds.setMaxIdle(50);
			ds.setMaxWait(10000);
			
			ds.setTestWhileIdle(true);
			ds.setTestOnBorrow(true);
			ds.setTestOnReturn(false);
			
			ds.setValidationQuery("SELECT 1 FROM DUAL");
			ds.setRemoveAbandoned(true);
			ds.setRemoveAbandonedTimeout(60 * 60);
			
			return ds;
		} catch (Throwable t) {
			throw new RuntimeException("failed to initialise JDBC driver", t);
		}
	}
	
	/* FFiorellato - removed */
	/*
	private PooledConnection newConnectionPool() {
		
		try {
			
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			
			log.trace("loading JDBC driver: " + ORACLE_JDBC_DRIVER + " using: " + loader);
			
			Class<?> jdbcDriverClass = loader.loadClass(ORACLE_JDBC_DRIVER);
			Driver driver = (Driver) jdbcDriverClass.newInstance();
			DriverManager.registerDriver(driver);
			
			return new OraclePooledConnection(properties.getProperty(CONFIG_ENDPOINT),
														properties.getProperty(CONFIG_USER),
														properties.getProperty(CONFIG_PWD));
			
		} catch (Exception e) {
			throw new RuntimeException("failed to initialise JDBC driver",e);
		}
	}
	*/
	
}


