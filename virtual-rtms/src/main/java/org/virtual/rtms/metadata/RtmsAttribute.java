/**
 * 
 */
package org.virtual.rtms.metadata;

import static org.virtual.rtms.Constants.*;

import org.virtual.rtms.RtmsConfiguration;
import org.virtualrepository.Properties;
import org.virtualrepository.Property;

/**
 * @author Fabrizio Sibeni
 *
 */
public class RtmsAttribute extends RtmsMetadataElement {

	private boolean isCode;
	private final RtmsConfiguration configuration;
	
	public RtmsAttribute(RtmsConfiguration configuration, String name, Properties properties) {
		
		super(name, properties);
		
		this.configuration=configuration;
		
	}
	
	public int id() {
		return properties().lookup(RTMS_ATTRIBUTE_ID).value(Integer.class);
	}
	
	public int conceptId() {
		return properties().lookup(RTMS_ATTRIBUTE_OBJID).value(Integer.class);
	}

	public RtmsInitialiser initialiser() {
		Property prop = properties().lookup(RTMS_ATTRIBUTE_READERINIT);
		return configuration.metadata().initialiser(prop.value(Integer.class));
	}
	
	public boolean isCode() {
		return isCode;
	}
	public void isCode(boolean value) {
		isCode=value;
	}
	
	@Override
	public String toString() {
		return isCode()?"*"+name():name();
	}
	
	
}
