package org.virtual.rtms;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.virtualrepository.spi.Browser;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.spi.Lifecycle;
import org.virtualrepository.spi.Publisher;
import org.virtualrepository.spi.ServiceProxy;

public class RtmsProxy implements ServiceProxy, Lifecycle {

	private static final String CONFIGURATION_FILE = "rtms.properties";

	private final RtmsBrowser browser = new RtmsBrowser();
	private final List<CsvPublisher> publishers = new ArrayList<CsvPublisher>();
	private final List<CsvImporter> importers = new ArrayList<CsvImporter>();
	
	@Override
	public void init() throws Exception {
	
		Properties properties = new Properties();
		
		try {
			properties.load(RtmsProxy.class.getClassLoader().getResourceAsStream(CONFIGURATION_FILE));
		}
		catch(Exception e) {
			throw new IllegalStateException("missing configuration: configuration file "+CONFIGURATION_FILE+" not on classpath");
		}
		
		RtmsConfiguration configuration = null;
		try {
			configuration = new RtmsConfiguration(properties);
		}
		catch(Exception e) {
			throw new IllegalStateException("invalid configuration (see cause) ",e);	
		}
		
		
		publishers.add(new CsvPublisher(configuration));
		importers.add(new CsvImporter(configuration));
		
	}
	
	
	@Override
	public Browser browser() {
		return browser;
	}

	@Override
	public List<? extends Importer<?, ?>> importers() {
		return importers;
	}

	@Override
	public List<? extends Publisher<?, ?>> publishers() {
		return publishers;
	}

	
	
}
