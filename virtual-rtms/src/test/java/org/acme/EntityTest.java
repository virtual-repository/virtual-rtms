package org.acme;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.virtual.rtms.model.CodedEntity;
import org.virtual.rtms.model.Codelist;
import org.virtual.rtms.model.Entity;
import org.virtualrepository.Asset;

public class EntityTest {

	@Test
	public void nullEntity() {
	
		Entity e = new Entity();
		
		assertEquals(null,e.id());
		assertEquals(null,e.name());
		assertEquals(null,e.table());
		assertEquals(null,e.column());
		
	}
	
	@Test
	public void entity() {
	
		Entity e = new Entity("id","name","table","col");
		
		assertEquals("id",e.id());
		assertEquals("name",e.name());
		assertEquals("table",e.table());
		assertEquals("col",e.column());
		
	}
	
	@Test
	public void equalEntities() {
	
		Entity e1 = new Entity("id","name","table","col");
		Entity e2 = new Entity("id","name","table","col");
		
		assertEquals(e1,e2);
	}
	
	
	@Test
	public void codedEntities() {
		
		CodedEntity e = new CodedEntity("pid","id", "name","table","col");
		
		e.attribute("aid", "aname", "acol");
		
		System.out.println(e);
		
		List<Codelist> ls = e.codelists();
		
		assertEquals(1,ls.size());
		
		Codelist l = ls.get(0);
		
		System.out.println(l);
		
		assertNotNull(l.query());
				
	}
	
	@Test
	public void codelists() {
		
		Codelist list = new CodedEntity("pid","id", "name","table","col")
						.attribute("aid", "aname", "acol")
						.codelists()
						.get(0);
		
		Asset csv = list.toCsvAsset();
		
		Codelist wrapped = new Codelist(csv);
		
		assertEquals(list.query(),wrapped.query());
		
		Asset sdmx = list.toSdmxAsset();
		
		wrapped = new Codelist(sdmx);
		
		assertEquals(list.query(),wrapped.query());
		
	}
	
}
