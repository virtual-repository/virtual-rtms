package org.acme;

import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Repository;

public class ImporterIntegrationTests {

	
	@Test
	public void discoverCsvCodelists() {
		
		VirtualRepository repo = new Repository();
		
		repo.discover(CsvCodelist.type);
		
		for (Asset asset : repo) 
			System.out.println(asset);
		
	}
}
