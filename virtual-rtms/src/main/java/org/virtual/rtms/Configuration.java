package org.virtual.rtms;

import static org.virtualrepository.Utils.*;

import java.util.Properties;

public class Configuration {

	public static final String endpoint = "figis.endpoint";
	public static final String driver = "figis.driver";
	public static final String url = "figis.url";
	public static final String user = "figis.user";
	public static final String pwd = "figis.pwd";
	public static final String validation_query = "figis.validation.query";
	public static final String no_refresh = "figis.norefresh";
	public static final String publish_host = "figis.publish.host";
	public static final String publish_user = "figis.publish.user";
	public static final String publish_pwd = "figis.publish.pwd";
	public static final String publish_path = "figis.publish.path";
	public static final String publish_timeout = "figis.publish.timeout";

	private final Properties properties;

	public Configuration(Properties properties) {

		validate(properties);

		this.properties = properties;
	}

	public String url() {
		return properties.getProperty(url);
	}

	public String driver() {
		return properties.getProperty(driver);
	}

	public String endpoint() {
		return properties.getProperty(endpoint);
	}

	public String user() {
		return properties.getProperty(user);
	}

	public String pwd() {
		return properties.getProperty(pwd);
	}

	public String validationQuery() {
		return properties.getProperty(validation_query);
	}

	public boolean noRefresh() {
		return properties.getProperty(no_refresh)!=null;
	}
	
	public String publishHost() {
		return properties.getProperty(publish_host);
	}
	
	public String publishUser() {
		return properties.getProperty(publish_user);
	}
	
	public String publishPwd() {
		return properties.getProperty(publish_pwd);
	}
	
	public String publishPath() {
		return properties.getProperty(publish_path);
	}
	
	public int publishTimeout() {
		return Integer.valueOf(properties.getProperty(publish_timeout));
	}

	private void validate(Properties properties) {

		notNull(endpoint, properties.getProperty(endpoint));
		notNull(driver, properties.getProperty(driver));
		notNull(url, properties.getProperty(url));
		notNull(user, properties.getProperty(user));
		notNull(pwd, properties.getProperty(pwd));
		
		notNull(publish_host, properties.getProperty(publish_host));
		notNull(publish_path, properties.getProperty(publish_path));
		notNull(publish_user, properties.getProperty(publish_user));
		notNull(publish_pwd, properties.getProperty(publish_pwd));
		notNull(publish_timeout, properties.getProperty(publish_timeout));
		
	}
}
