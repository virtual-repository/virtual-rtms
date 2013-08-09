package org.virtual.rtms;

import static org.virtual.rtms.Constants.*;
import static org.virtualrepository.Utils.*;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.metadata.RtmsMetadata;
import org.virtual.rtms.metadata.RtmsMetadataBrowser;

public class RtmsConfiguration {

	private static Logger log = LoggerFactory.getLogger(RtmsConfiguration.class);
	
	private final Properties properties;
	private RtmsMetadata metadata;
	private DataSource dataSource;
	
	public RtmsConfiguration(Properties properties) {
		
		this.properties=properties;
		
		validateStaticConfiguration(properties);
		
		this.dataSource = this.setupDataSource();
	}
	
	public Connection connection() {
		
		try {
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
		
		//Should we assume that the DB VALIDATION QUERY can be optional?
		//notNull(CONFIG_DB_VALIDATION_QUERY, properties.getProperty(CONFIG_DB_VALIDATION_QUERY));
	}
	
	/**
	 * @return a basic data source (see commons-dbcp) that acts as a (configurable) connection pool. 
	 * You probably want to change a few configuration parameters, though. These are proven to work fine. 
	 */
	public DataSource setupDataSource() {
		
		try {
			log.trace("Initializing DBCP data source with:");
			log.trace(" # Driver class name ({}): {}", CONFIG_DRIVER, properties.getProperty(CONFIG_DRIVER));
			log.trace(" # URL               ({}): {}", CONFIG_ENDPOINT, properties.getProperty(CONFIG_ENDPOINT));
			log.trace(" # User name         ({}): {}", CONFIG_USER, properties.getProperty(CONFIG_USER));
			log.trace(" # Password          ({}): {}", CONFIG_PWD, properties.getProperty(CONFIG_PWD));
			log.trace(" # Validation query  ({}): {}", CONFIG_DB_VALIDATION_QUERY, properties.getProperty(CONFIG_DB_VALIDATION_QUERY));
			
			BasicDataSource ds = new BasicDataSource();
			ds.setDriverClassName(properties.getProperty(CONFIG_DRIVER));
			ds.setUrl(properties.getProperty(CONFIG_ENDPOINT));
			ds.setUsername(properties.getProperty(CONFIG_USER));
			ds.setPassword(properties.getProperty(CONFIG_PWD));
				
			ds.setMaxActive(50);
			ds.setMaxIdle(50);
			ds.setMaxWait(10000);
			
			ds.setTestWhileIdle(true);
			ds.setTestOnBorrow(true);
			ds.setTestOnReturn(false);
			
			if(properties.getProperty(CONFIG_DB_VALIDATION_QUERY) != null)
				ds.setValidationQuery(properties.getProperty(CONFIG_DB_VALIDATION_QUERY));
			ds.setRemoveAbandoned(true);
			ds.setRemoveAbandonedTimeout(60 * 60);
			
			return ds;
		} catch (Throwable t) {
			throw new RuntimeException("failed to initialise JDBC driver", t);
		}
	}
	
}


