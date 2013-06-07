package org.virtual.rtms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	private static final Logger log = LoggerFactory.getLogger(RtmsPlugin.class);
	
	public static void cleanup(ResultSet rs) {
		if (rs!=null)
			try {
				rs.close();
			}
			catch(Exception e) {
				log.error("cannot close a JDBC resultset from RTMS (see cause)",e);
			}
	}
	
	public static void cleanup(Statement stmnt, ResultSet rs) {
		
		cleanup(rs);
		
		if (stmnt!=null)
			try {
				stmnt.close();
			}
			catch(Exception e) {
				log.error("cannot close a JDBC statement for RTMS (see cause)",e);
			}
	}
	
	public static void cleanup(Connection connection, Statement stmnt, ResultSet rs) {
		cleanup(stmnt,rs);
		cleanup(connection);
	}
	
	public static void cleanup(Connection connection) {
		if (connection!=null)
			try {
				connection.close();
			}
			catch(Exception e) {
				log.error("cannot close a JDBC connection to RTMS (see cause)",e);
			}
	}
}
