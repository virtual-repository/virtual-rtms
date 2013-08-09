package org.acme;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.sdmxsource.sdmx.api.constants.STRUCTURE_OUTPUT_FORMAT;
import org.sdmxsource.sdmx.api.manager.output.StructureWritingManager;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.structureparser.manager.impl.StructureWritingManagerImpl;
import org.sdmxsource.sdmx.util.beans.container.SdmxBeansImpl;
import org.virtualrepository.Asset;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.csv.CsvAsset;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Repository;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

public class RetrievalIntegrationTests {

	
	@Test
	public void retrieveFirstTableCodelist() {
		
		VirtualRepository repo = new Repository();
		
		repo.discover(CsvCodelist.type);
		
		Iterator<Asset> it = repo.iterator();
		
		Asset codelist = it.next();
		
		Table table = repo.retrieve(codelist,Table.class);
		
		System.out.println(table.columns());
		
		for (Row row : table)
			System.out.println(row);
	}
	
	@Test
	public void retrieveNamedCodelist() {
	
		String name = "FIRMS Data Collection - Code";
		
		VirtualRepository repo = new Repository();
		
		repo.discover(CsvCodelist.type);
		
		for (Asset a : repo)
			if (a.name().equals(name)) {
				System.out.println(repo.retrieve(a, Table.class));
				return;
			}
		
		fail("unknown codelist");
	}
	
	@Test
	public void retrieveCodelistbyId() {
	
		String id = "rtms-263000-12";
		
		VirtualRepository repo = new Repository();
		
		repo.discover(CsvCodelist.type);
		
		Asset asset  = repo.lookup(id);
		System.out.println(repo.retrieve(asset, Table.class));
		
	}
	
	@Test
	public void retrieveAllTableCodelist() {
		
		VirtualRepository repo = new Repository();
		
		long discoveryTime = System.currentTimeMillis();
		
		repo.discover(CsvCodelist.type);
		
		discoveryTime=System.currentTimeMillis()-discoveryTime;
		
		System.out.println(discoveryTime);
		
		List<String> slow = new ArrayList<String>();
		List<String> good = new ArrayList<String>();
		List<String> empties = new ArrayList<String>();
		List<String> failures = new ArrayList<String>();
		List<String> undescribed = new ArrayList<String>();
		
		long duration=0;
		int count=1;
		int max=5;
		for (Asset asset : repo) {
			
			if (count>=max)
				break;
			
			try {
				
				CsvAsset csv = (CsvAsset) asset;
				
				long time = System.currentTimeMillis();
				
				Table table = repo.retrieve(asset,Table.class);
	
				time = System.currentTimeMillis()-time; 
				
				duration=duration+time;
				
				if (time >3000)
					slow.add(asset.name());
				
				if (table.columns().isEmpty())
					undescribed.add(asset.name());
	
				if (!table.iterator().hasNext())
					empties.add(csv.name());
				else
					good.add(asset.name());
			}
			catch(Exception e) {
				System.err.println("failed retrieving "+asset.name()+":"+e.getClass().getSimpleName()+":"+e.getMessage());
				failures.add(asset.name());
			}
			finally {
				count++;
			}
			
			
		}
		
		System.out.println("retrieved "+count);
		System.out.println("discovered in "+discoveryTime);
		System.out.println(good.size()+" readable codelists: ");
		System.out.println(empties.size()+" empty codelists: "+empties);
		System.out.println(failures.size()+" unreadable codelists: "+failures);
		System.out.println(undescribed.size()+" undescribed codelists: "+undescribed);
		System.out.println(slow.size()+" slow codelists (>3sec)"+slow);
		System.out.println("average retrieval time:"+duration/count);
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
