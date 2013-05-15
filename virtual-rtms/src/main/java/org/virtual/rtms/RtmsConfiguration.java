package org.virtual.rtms;

import static org.virtualrepository.Utils.*;

import java.net.URI;
import java.util.Properties;

public class RtmsConfiguration {

	private final Properties properties;
	
	public RtmsConfiguration(Properties properties) {
		this.properties=properties;
		
		notNull("configuration", properties);
		notNull("endpoint", properties.getProperty("endpoint"));
	}
	
	public URI endpoint() {
		return URI.create(properties.getProperty("endpoint"));
	}
	
}
