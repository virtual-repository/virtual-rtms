package org.acme;

import static java.lang.System.*;
import static java.util.Arrays.*;
import static org.acme.Utils.*;
import static org.junit.Assert.*;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.Dependencies;
import org.virtual.rtms.Rtms;
import org.virtual.rtms.RtmsBrowser;
import org.virtual.rtms.RtmsConnection;
import org.virtual.rtms.codelist.CodelistImporter;
import org.virtual.rtms.model.Codelist;
import org.virtualrepository.Asset;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

import dagger.Module;

@Module(injects=IntegrationTests.class, includes=Dependencies.class)
public class IntegrationTests {

	private static Logger log;
	
	@Inject
	Rtms rtms;
	
	@Inject
	RtmsBrowser browser;
	
	@Inject
	CodelistImporter importer;
	
	@BeforeClass
	public static void setup() {
		
		setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
		log = LoggerFactory.getLogger("test");
		
	}
	
	@Test
	public void connects() throws Exception {
	
		inject(this);
		
		try (
			RtmsConnection c = rtms.connect()
		)
		{}
	}
	
	@Test
	public void findsCodelists() throws Exception {
	
		inject(this);
		
		try (
			
			RtmsConnection c = rtms.connect()
		
		)
		{
			
			Collection<Codelist> result = c.codelists();
			
			log.info("test found {} codelists",result.size());

			for (Codelist list : result)
				System.out.println(list);
		}
	}
	
	
	@Test
	public void browseCsvCodelists() throws Exception {
	
		inject(this);
		
		for (Asset list: browser.discover(asList(CsvCodelist.type)))
				System.out.println(list);
		
	}
	
	@Test
	public void retrieveFirstCodelist() throws Exception {
	
		inject(this);
		
		Iterable<? extends Asset> codelists = browser.discover(asList(CsvCodelist.type));
		
		Iterator<? extends Asset> it = codelists.iterator();
		
		it.next();
		
		Asset codelist = it.next();
		
		Table table = importer.retrieve(codelist);
	
		for (Row row : table)
			System.out.println(row);
	}
	

	@Test
	public void retrieveAllCodelists() throws Exception {
	
		inject(this);
		
		Iterable<? extends Asset> codelists = browser.discover(asList(CsvCodelist.type));
		
		Iterator<? extends Asset> it = codelists.iterator();
		
		List<String> empties = new ArrayList<>();
		Map<String,String> errors = new HashMap<>();
		
		while (it.hasNext()) {
			Asset asset = it.next();
			try {
				Table table = importer.retrieve(asset);
				
				if (!table.iterator().hasNext())
					empties.add(asset.id());
				
			}
			catch(Exception e) {
				errors.put(asset.id(),e.getMessage());
			}
		}
		
		System.out.println(empties.size()+" empties\n:"+empties);
		
		assertEquals(errors.toString(),0,errors.size());
	}
	
	@Test
	public void browseSdmxCodelists() throws Exception {
	
		inject(this);
		
		for (Asset list: browser.discover(Arrays.asList(SdmxCodelist.type)))
				System.out.println(list);
		
	}

}
