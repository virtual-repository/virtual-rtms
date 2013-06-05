package org.acme;

import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Repository;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

public class RetrievalIntegrationTests {

	
	@Test
	public void retrieveTableCodelists() {
		
		VirtualRepository repo = new Repository();
		
		repo.discover(CsvCodelist.type);
		
		Asset codelist = repo.iterator().next();
		
		System.out.println(codelist.type());
		
		Table table = repo.retrieve(codelist,Table.class);
		
		System.out.println(table.columns());
		for (Row row : table)
			System.out.println(row);
	}
}
