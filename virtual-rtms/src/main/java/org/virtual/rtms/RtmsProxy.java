package org.virtual.rtms;

import static org.virtualrepository.spi.ImportAdapter.*;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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


/**
 * Configures and provides access components.
 *  
 * @author Fabio Simeoni
 *
 */
public class RtmsProxy implements ServiceProxy, Lifecycle {

	
	private static Logger log = LoggerFactory.getLogger(RtmsBrowser.class);

	@Inject
	Configuration configuration;
	
	@Inject
	RtmsBrowser browser;
	
	@Inject
	CsvCodelistImporter csvImporter;
	
	@Inject
	SdmxCodelistImporter sdmxImporter;
	
	private final List<Publisher<?,?>> publishers = new ArrayList<Publisher<?,?>>();
	private final List<Importer<?,?>> importers = new ArrayList<Importer<?,?>>();

	@Override
	public void init() throws Exception {

		log.info("connecting to rtms @ {}",configuration.url());

		importers.add(csvImporter);
		importers.add(sdmxImporter);

		//derived stream importer
		importers.add(adapt(csvImporter, new Table2CsvStream<CsvCodelist>()));

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
