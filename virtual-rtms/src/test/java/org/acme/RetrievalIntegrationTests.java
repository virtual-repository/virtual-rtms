package org.acme;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import org.junit.Test;
import org.sdmxsource.sdmx.api.constants.STRUCTURE_OUTPUT_FORMAT;
import org.sdmxsource.sdmx.api.manager.output.StructureWritingManager;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.structureparser.manager.impl.StructureWritingManagerImpl;
import org.sdmxsource.sdmx.util.beans.container.SdmxBeansImpl;
import org.virtualrepository.Asset;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Repository;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

public class RetrievalIntegrationTests {

	
	@Test
	public void retrieveTableCodelist() {
		
		VirtualRepository repo = new Repository();
		
		repo.discover(CsvCodelist.type);
		
		Iterator<Asset> it = repo.iterator();
		it.next();
		
		Asset codelist = it.next();
		
		Table table = repo.retrieve(codelist,Table.class);
		
		System.out.println(table.columns());
		for (Row row : table)
			System.out.println(row);
	}
	
	@Test
	public void retrieveAllTableCodelist() {
		
		VirtualRepository repo = new Repository();
		
		repo.discover(CsvCodelist.type);
		
		for (Asset asset : repo) {
			Table table = repo.retrieve(asset,Table.class);
			for (Row row : table)
				System.out.println(row);
		}
	}
	
	@Test
	public void retrieveAllTableCodelists() throws Exception	{
		
		VirtualRepository repo = new Repository();
		
		repo.discover(CsvCodelist.type);
		
		int counter = 0;
		
		for (Asset asset : repo) {
			System.out.println("loading asset "+asset.id());
			repo.retrieve(asset,Table.class);
			System.out.println("loaded table n."+counter);
			counter++;
			
		}
	}
	
	@Test
	public void retrieveSdmxCodelist() {
		
		VirtualRepository repo = new Repository();
		
		repo.discover(SdmxCodelist.type);
		
		Asset codelist = repo.iterator().next();
		
		CodelistBean bean = repo.retrieve(codelist,CodelistBean.class);
		
		toXml(bean);
		
	}
	
	void toXml(CodelistBean list) {

		SdmxBeans beans = new SdmxBeansImpl(list);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
		
		STRUCTURE_OUTPUT_FORMAT format = STRUCTURE_OUTPUT_FORMAT.SDMX_V21_STRUCTURE_DOCUMENT;
		
		StructureWritingManager manager = new StructureWritingManagerImpl();
		manager.writeStructures(beans,format, stream);
		
		System.out.println(stream.toString());
	}
}
