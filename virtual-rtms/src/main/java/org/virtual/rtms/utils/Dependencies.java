package org.virtual.rtms.utils;

import java.io.InputStream;
import java.security.PublicKey;
import java.sql.Connection;
import java.util.Properties;

import javax.inject.Singleton;
import javax.sql.DataSource;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

import org.apache.commons.dbcp.BasicDataSource;
import org.sdmx.SdmxServiceFactory;
import org.sdmxsource.sdmx.api.manager.output.StructureWriterManager;
import org.virtual.rtms.Configuration;
import org.virtual.rtms.RtmsPlugin;

import dagger.Module;
import dagger.Provides;

/**
 * Dependencies provided to Dagger for injection.
 */
@Module(injects = RtmsPlugin.class)
public class Dependencies {

	public static final String path = "/rtms.properties";

	@Provides
	@Singleton
	public Configuration configuration() {

		InputStream stream = getClass().getResourceAsStream(path);

		if (stream == null)
			throw new IllegalStateException("missing configuration: configuration resource " + path + " not on classpath");

		Properties properties = new Properties();

		try {

			properties.load(stream);

		} catch (Exception e) {

			throw new IllegalStateException("cannot read configuration resources at " + path);

		}

		return new Configuration(properties);

	};

	/**
	 * @return a basic data source (see commons-dbcp) that acts as a
	 *         (configurable) connection pool. You may want to change a few
	 *         configuration parameters, even though these are proven to work
	 *         fine.
	 */
	@Provides
	@Singleton
	public DataSource source(Configuration configuration) {

		try {

			BasicDataSource ds = new BasicDataSource();
			ds.setDriverClassName(configuration.driver());
			ds.setUrl(configuration.endpoint());
			ds.setUsername(configuration.user());
			ds.setPassword(configuration.pwd());

			ds.setMaxActive(50);
			ds.setMaxIdle(50);
			ds.setMaxWait(10000);

			ds.setTestWhileIdle(true);
			ds.setTestOnBorrow(true);
			ds.setTestOnReturn(false);

			String query = configuration.validationQuery();
			if (query != null)
				ds.setValidationQuery(query);
			ds.setRemoveAbandoned(true);
			ds.setRemoveAbandonedTimeout(60 * 60);

			return ds;
		} catch (Throwable t) {

			throw new RuntimeException("failed to initialise JDBC driver", t);
		}

	}

	@Provides
	public Connection sqlConnection(DataSource source) {

		try {

			return source.getConnection();

		} catch (Exception e) {

			throw new RuntimeException("cannot connect to rtms (see cause)", e);

		}
	}

	@Provides
	@Singleton
	public StructureWriterManager writer() {

		return SdmxServiceFactory.writer();
	}

	private static final HostKeyVerifier noop = new HostKeyVerifier() {

		@Override
		public boolean verify(String hostname, int port, PublicKey key) {
			return true;
		}
	};

	@Provides
	public SSHClient sshClient(Configuration configuration) {

		try {
			
			SSHClient client = new SSHClient();

			client.addHostKeyVerifier(noop);

			client.connect(configuration.publishHost());

			client.authPassword(configuration.publishUser(), configuration.publishPwd());
			
			client.setTimeout(configuration.publishTimeout());
			
			return client;
			
		} catch (Exception e) {
			throw new RuntimeException("cannot configure ssh client ", e);
		}

	}

}
