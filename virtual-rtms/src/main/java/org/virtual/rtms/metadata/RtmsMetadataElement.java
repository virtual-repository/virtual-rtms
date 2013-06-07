package org.virtual.rtms.metadata;

import org.virtualrepository.Properties;

public class RtmsMetadataElement {

	private final String name;
	private final Properties properties;
	
	public RtmsMetadataElement(String name, Properties properties) {
		this.name=name;
		this.properties=properties;
	}
	
	public String name() {
		return name;
	}
	
	public Properties properties() {
		return properties;
	}
	
}
