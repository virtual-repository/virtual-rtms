package org.virtual.rtms;

import static org.virtualrepository.spi.ImportAdapter.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.codelist.CsvCodelistImporter;
import org.virtual.rtms.codelist.SdmxCodelistImporter;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.csv.Table2CsvStream;
import org.virtualrepository.spi.Browser;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.spi.Lifecycle;
import org.virtualrepository.spi.Publisher;
import org.virtualrepository.spi.ServiceProxy;



public class RtmsProxy implements ServiceProxy, Lifecycle {

	

	Logger log = LoggerFactory.getLogger(RtmsBrowser.class);

	private static final String CONFIGURATION_FILE = "rtms.properties";

	private RtmsBrowser browser;
	private final List<Publisher<?,?>> publishers = new ArrayList<Publisher<?,?>>();
	private final List<Importer<?,?>> importers = new ArrayList<Importer<?,?>>();

	private static RtmsConfiguration configuration = null;

	@Override
	public void init() throws Exception {

		Properties properties = new Properties();	

		try {
			properties.load(RtmsProxy.class.getClassLoader().getResourceAsStream(CONFIGURATION_FILE));
		}
		catch(Exception e) {
			throw new IllegalStateException("missing configuration: configuration file " + CONFIGURATION_FILE+" not on classpath");
		}

		try {
			configuration = new RtmsConfiguration(properties);
			log.info("connecting to FIGIS database @ {}",configuration.url());
		}
		catch(Exception e) {
			throw new IllegalStateException("invalid configuration (see cause) ",e);	
		}


		browser = new RtmsBrowser(configuration);
		
		//tabular importer
		CsvCodelistImporter baseImporter = new CsvCodelistImporter(configuration);
		importers.add(baseImporter);
		
		//derived stream importer
		importers.add(adapt(baseImporter, new Table2CsvStream<CsvCodelist>()));
		
		//derived sdmx imported
		importers.add(new SdmxCodelistImporter(configuration));

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
