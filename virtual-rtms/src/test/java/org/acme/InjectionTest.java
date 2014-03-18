package org.acme;


import static java.lang.System.*;
import static org.acme.Utils.*;
import static org.junit.Assert.*;
import static org.virtual.rtms.RtmsPlugin.*;

import javax.inject.Inject;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sdmxsource.sdmx.api.manager.output.StructureWriterManager;
import org.virtual.rtms.Configuration;
import org.virtual.rtms.Rtms;
import org.virtual.rtms.RtmsPlugin;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.impl.Repository;

import dagger.Module;

@Module(injects=InjectionTest.class, includes=RtmsPlugin.class)
public class InjectionTest {

	@Inject
	Configuration config;
	
	@Inject
	Rtms rtms;
	
	@Inject
	StructureWriterManager manager;
	
	@BeforeClass
	public static void setup() {
		
		setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
		setProperty("org.slf4j.simpleLogger.log.org.springframework", "warn");
	
	}
	
	@Test
	public void injectionsWork() throws Exception {
	
		inject(this);
		
		assertNotNull(config);
		assertNotNull(rtms);
		assertNotNull(manager);
	}
	
	@Test
	public void pluginStarts() throws Exception {
	
		VirtualRepository repo = new Repository();
		
		RepositoryService service = repo.services().lookup(name);
		
		System.out.println(service.properties());
		
	}

}
