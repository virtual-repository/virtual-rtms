package org.virtual.rtms;

import java.sql.Connection;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;


@Singleton
public class Rtms {

	private final Provider<Connection> connections;

	@Inject
	public Rtms(Provider<Connection> connections) {
		this.connections=connections;
	}
	
	public RtmsConnection connect() {	
		return new RtmsConnection(connections.get());
	}
	
}
