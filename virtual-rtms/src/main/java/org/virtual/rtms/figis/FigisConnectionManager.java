/**
 * 
 */
package org.virtual.rtms.figis;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.PooledConnection;

import oracle.jdbc.pool.OraclePooledConnection;

import org.virtual.rtms.RtmsProxy;

/**
 * @author Fabrizio Sibeni
 *
 */
public class FigisConnectionManager {
	public static final String ORACLE_JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	public static final String ORACLE_POOLED_CONNECTION = "oracle.jdbc.pool.OraclePooledConnection";

	
	private static FigisConnectionManager instance = null;
	
	
	private Driver driver = null;
	private PooledConnection connectionsPool = null;
	

	
	
	private FigisConnectionManager() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

	
	
	public static FigisConnectionManager getInstance() {
		if (instance==null){
			instance = new FigisConnectionManager();
		}
		return instance;
	}





	public synchronized Connection getConnection() throws SQLException {
		Connection connection = null;
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			System.out.println("RM 22-05-2013 16.20: loading JDBC driver: " + ORACLE_JDBC_DRIVER + " using: " + loader);
			Class<?> jdbcDriverClass = loader.loadClass(ORACLE_JDBC_DRIVER);
			driver = (Driver) jdbcDriverClass.newInstance();
			DriverManager.registerDriver( driver );
			@SuppressWarnings("unused")
			Constructor<?> c = Class.forName(ORACLE_POOLED_CONNECTION).getConstructor(String.class, String.class, String.class);
			connectionsPool = new OraclePooledConnection(RtmsProxy.getConfiguration().url() ,RtmsProxy.getConfiguration().username(),RtmsProxy.getConfiguration().password());
		} catch (Exception e) {
			System.out.println( "Failed to initialise JDBC driver" );
			e.printStackTrace();
		}

		if (connection==null || !connection.isValid(0)) {
			connection = connectionsPool.getConnection();
		}

		return connection;
	}

	
	
	
}
