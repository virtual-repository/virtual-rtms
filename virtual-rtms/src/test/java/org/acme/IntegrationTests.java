package org.acme;

import static java.lang.System.*;
import static java.util.Arrays.*;
import static org.acme.Utils.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sdmxsource.sdmx.api.constants.STRUCTURE_OUTPUT_FORMAT;
import org.sdmxsource.sdmx.api.manager.output.StructureWriterManager;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.sdmxbeans.model.SdmxStructureFormat;
import org.sdmxsource.sdmx.util.beans.container.SdmxBeansImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.BaseImporter;
import org.virtual.rtms.Rtms;
import org.virtual.rtms.RtmsBrowser;
import org.virtual.rtms.RtmsConnection;
import org.virtual.rtms.RtmsPlugin;
import org.virtual.rtms.RtmsProxy;
import org.virtual.rtms.model.Codelist;
import org.virtualrepository.Asset;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.csv.CsvTable;
import org.virtualrepository.impl.Repository;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

import dagger.Module;

@Module(injects = IntegrationTests.class, includes = RtmsPlugin.class)
public class IntegrationTests {

	private static Logger log;

	@Inject
	Rtms rtms;

	@Inject
	RtmsBrowser browser;

	@Inject
	BaseImporter importer;
	
	@Inject
	StructureWriterManager manager;
	
	@Inject
	RtmsProxy proxy;
	
	@BeforeClass
	public static void setup() {

		setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
		setProperty("org.slf4j.simpleLogger.log.org.springframework", "warn");
		log = LoggerFactory.getLogger("test");

	}

	@Test
	public void connects() throws Exception {

		inject(this);

		try (RtmsConnection c = rtms.connect()) {
		}
	}

	@Test
	public void findsCodelists() throws Exception {

		inject(this);

		try (

		RtmsConnection c = rtms.connect()

		) {

			Collection<Codelist> result = c.codelists();

			log.info("test found {} codelists", result.size());

			for (Codelist list : result)
				log.debug(list.toString());
		}
	}

	@Test
	public void discoverCsvCodelists() {

		VirtualRepository repo = new Repository();

		repo.discover(CsvCodelist.type);

		for (Asset asset : repo)
			log.debug(asset.toString());

	}

	@Test
	public void prefersCsvCodelists() {

		VirtualRepository repo = new Repository();

		repo.discover(CsvCodelist.type, SdmxCodelist.type);

		for (Asset asset : repo)
			assertEquals(CsvCodelist.type, asset.type());

	}

	@Test
	public void discoverSdmxCodelists() {

		VirtualRepository repo = new Repository();

		repo.discover(SdmxCodelist.type);

		for (Asset asset : repo)
			log.debug(asset.toString());

	}

	@Test
	public void retrieveFirstCodelist() throws Exception {

		inject(this);

		Iterable<? extends Asset> codelists = browser.discover(asList(CsvCodelist.type));

		Iterator<? extends Asset> it = codelists.iterator();

		Asset codelist = it.next();

		Table table = importer.retrieve(codelist);

		for (Row row : table)
			log.debug(row.toString());
		
	}
	
	@Test
	public void retrieveGivenCodelist() throws Exception {

		inject(this);

		String name = "rtms-Gear category (intl.)-ISSCFG Code";

		Iterable<? extends Asset> codelists = browser.discover(asList(CsvCodelist.type));

		for (Asset list  : codelists) 
			if (list.name().equals(name)) {
				Table table = importer.retrieve(list);
				for (Row row : table)
					log.debug(row.toString());
			}
		
	}
	
	

	@Test
	public void retrieveFirstCodelistAsTable() throws Exception {

		VirtualRepository repo = new Repository();

		repo.discover(CsvCodelist.type);

		Asset list = repo.iterator().next();
		
		Table table = repo.retrieve(list,Table.class);

		log.debug(list.toString());
		
		for (Row row : table)
			log.debug(row.toString());
	}
	
	@Test
	public void retrieveFirstCodelistAsStream() throws Exception {

		VirtualRepository repo = new Repository();

		repo.discover(CsvCodelist.type);

		Asset list = repo.iterator().next();
		
		InputStream stream = repo.retrieve(list,InputStream.class);

		Table table = new CsvTable((CsvCodelist)list, stream);
		
		for (Row row : table)
			System.out.println(row);
	}
	
	@Test
	public void retrieveFirstCodelistAsSdmx() throws Exception {
		
		inject(this);
		
		VirtualRepository repo = new Repository();

		repo.discover(SdmxCodelist.type);

		CodelistBean bean = repo.retrieve(repo.iterator().next(),CodelistBean.class);
		
		SdmxBeans beans = new SdmxBeansImpl(bean);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
		
		STRUCTURE_OUTPUT_FORMAT format = STRUCTURE_OUTPUT_FORMAT.SDMX_V21_STRUCTURE_DOCUMENT;
		
		manager.writeStructures(beans, new SdmxStructureFormat(format), stream);
		
		log.debug(stream.toString());

	}

	@Test
	public void retrieveAllCodelists() throws Exception {

		inject(this);

		Iterable<? extends Asset> codelists = browser.discover(asList(CsvCodelist.type));

		Iterator<? extends Asset> it = codelists.iterator();

		List<String> empties = new ArrayList<>();
		Map<String, String> errors = new HashMap<>();

		while (it.hasNext()) {
			Asset asset = it.next();
			try {
				Table table = importer.retrieve(asset);

				if (!table.iterator().hasNext())
					empties.add(asset.id());

			} catch (Exception e) {
				errors.put(asset.id(), e.getMessage());
			}
		}

		log.debug(empties.size() + " empties\n:" + empties);

		assertEquals(errors.toString(), 0, errors.size());
	}

	@Test
	public void browseSdmxCodelists() throws Exception {

		inject(this);

		for (Asset list : browser.discover(Arrays.asList(SdmxCodelist.type)))
			log.debug(list.toString());

	}
	
	@Test
	public void publishBackFirstCodelistAsSdmx() throws Exception {
		
		VirtualRepository repo = new Repository();

		repo.discover(SdmxCodelist.type);

		Asset list = repo.iterator().next();
		
		CodelistBean table = repo.retrieve(list,CodelistBean.class);

		repo.publish(list,table);
		
		
	}
	
	@Test
	public void publishBackFirstCodelistAsCsv() throws Exception {
		
		VirtualRepository repo = new Repository();

		repo.discover(CsvCodelist.type);

		Asset list = repo.iterator().next();
		
		Table table = repo.retrieve(list,Table.class);

		repo.publish(list,table);
		
	}


}
