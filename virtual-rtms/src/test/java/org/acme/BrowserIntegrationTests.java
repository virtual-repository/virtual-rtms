package org.acme;


import static org.junit.Assert.*;

import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Repository;
import org.virtualrepository.sdmx.SdmxCodelist;

public class BrowserIntegrationTests {

	
	@Test
	public void discoverCsvCodelists() {
		
		VirtualRepository repo = new Repository();
		
		repo.discover(CsvCodelist.type);
		
	}
	
	@Test
	public void prefersCsvCodelists() {
		
		VirtualRepository repo = new Repository();
		
		repo.discover(CsvCodelist.type,SdmxCodelist.type);
		
		for (Asset asset : repo)
			assertEquals(CsvCodelist.type, asset.type());
		
	}
	
	@Test
	public void discoverSdmxCodelists() {
		
		VirtualRepository repo = new Repository();
		
		repo.discover(SdmxCodelist.type);
		
		for (Asset asset : repo) 
			System.out.println(asset);
		
	}
}
