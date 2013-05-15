package org.acme;

import static org.junit.Assert.*;

import org.junit.Test;
import org.virtualrepository.impl.Services;


public class PluginTest {

	
	@Test
	public void pluginLoads() {
		
		Services repos = new Services();
		repos.load();
		
		assertTrue(repos.size()>0);
		
	}
}
