package org.virtual.rtms;

import static org.virtual.rtms.Constants.*;
import static org.virtualrepository.Utils.*;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

import javax.sql.PooledConnection;

import oracle.jdbc.pool.OraclePooledConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.metadata.RtmsMetadata;
import org.virtual.rtms.metadata.RtmsMetadataBrowser;

public class RtmsConfiguration {

	private static Logger log = LoggerFactory.getLogger(RtmsConfiguration.class);
	
	public static final String ORACLE_JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	
	private final PooledConnection connectionsPool;
	private final Properties properties;
	private RtmsMetadata metadata;
	
	public RtmsConfiguration(Properties properties) {
		
		this.properties=properties;
		
		validateStaticConfiguration(properties);
		
		//init pool
		this.connectionsPool = newConnectionPool();
		
	}
	
	public Connection connection() {
		
		try {
			return connectionsPool.getConnection();
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
	
	
}


