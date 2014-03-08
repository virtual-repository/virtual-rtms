package org.acme;


import static java.lang.System.*;
import static org.acme.Utils.*;
import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.BeforeClass;
import org.junit.Test;
import org.virtual.rtms.Configuration;
import org.virtual.rtms.Dependencies;
import org.virtual.rtms.Rtms;

import dagger.Module;

@Module(injects=InjectionTest.class, includes=Dependencies.class)
public class InjectionTest {

	@Inject
	Configuration config;
	
	@Inject
	Rtms rtms;
	
	@BeforeClass
	public static void setup() {
		
		setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
		
	}
	
	@Test
	public void injectionsWork() throws Exception {
	
		inject(this);
		
		assertNotNull(config);
		assertNotNull(rtms);
		
	}

}
