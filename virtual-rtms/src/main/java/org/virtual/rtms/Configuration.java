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

	private void validate(Properties properties) {

		notNull(endpoint, properties.getProperty(endpoint));
		notNull(driver, properties.getProperty(driver));
		notNull(url, properties.getProperty(url));
		notNull(user, properties.getProperty(user));
		notNull(pwd, properties.getProperty(pwd));

		// Should we assume that the DB VALIDATION QUERY can be optional?
		// notNull(CONFIG_DB_VALIDATION_QUERY,
		// properties.getProperty(CONFIG_DB_VALIDATION_QUERY));
	}
}
