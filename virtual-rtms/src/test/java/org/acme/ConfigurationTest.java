package org.acme;

import static org.junit.Assert.*;
import static org.virtual.rtms.Configuration.*;

import java.util.Properties;

import org.junit.Test;
import org.virtual.rtms.Configuration;
import org.virtual.rtms.Dependencies;

public class ConfigurationTest {

	@Test(expected=Exception.class)
	public void detectsInvalidConfiguration() {
		
		new Configuration(new Properties());
	}
		
	@Test
	public void readsValidConfiguration() {
		
		Properties properties = new Properties();
		
		properties.put(driver, "driver");
		properties.put(url, "url");
		properties.put(user, "user");
		properties.put(pwd, "pwd");
		properties.put(endpoint, "endpoint");
		
		Configuration c = new Configuration(properties);
		
		assertEquals(c.driver(),properties.get(driver));
		assertEquals(c.url(),properties.get(url));
		assertEquals(c.endpoint(),properties.get(endpoint));
		assertEquals(c.user(),properties.get(user));
		assertEquals(c.pwd(),properties.get(pwd));
	}
	
	@Test
	public void parsesConfigurationFromClasspath() {
		
		Configuration c = new Dependencies().configuration();

		assertNotNull(c.driver());
		assertNotNull(c.url());
		assertNotNull(c.endpoint());
		assertNotNull(c.user());
		assertNotNull(c.pwd());
		
	}
		
}
