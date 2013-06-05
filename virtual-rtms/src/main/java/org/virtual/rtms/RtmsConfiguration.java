package org.virtual.rtms;

import static org.virtualrepository.Utils.notNull;

import java.net.URI;
import java.util.Properties;

public class RtmsConfiguration {

	private final Properties properties;
	
	public RtmsConfiguration(Properties properties) {
		this.properties=properties;
		notNull("configuration", properties);
		notNull("figis.endpoint", properties.getProperty("figis.endpoint"));
		notNull("figis.jdbcdriver", properties.getProperty("figis.jdbcdriver"));
		notNull("figis.url", properties.getProperty("figis.url"));
		notNull("figis.schema", properties.getProperty("figis.schema"));
		notNull("figis.username", properties.getProperty("figis.username"));
		notNull("figis.password", properties.getProperty("figis.password"));
	}
	
	public URI endpoint() {
		return URI.create(properties.getProperty("figis.endpoint"));
	}
	
	public String jdbcdriver() {
		return properties.getProperty("figis.jdbcdriver");
	}

	public String url(){
		return properties.getProperty("figis.url");
	}
	
	public String schema(){
		return properties.getProperty("figis.schema");
	}

	public String username(){
		return properties.getProperty("figis.username");
	}
	
	public String password(){
		return properties.getProperty("figis.password");
	}
	
}


