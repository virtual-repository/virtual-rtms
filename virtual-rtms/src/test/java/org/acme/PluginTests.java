package org.acme;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.RtmsBrowser;
import org.virtual.rtms.figis.FigisConnectionManager;
import org.virtual.rtms.figis.RtmsCodelistFinder;
import org.virtual.rtms.figis.codelist.CodelistService;
import org.virtual.rtms.figis.codelist.CodelistServiceFactory;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.AbstractType;
import org.virtualrepository.impl.Services;
import org.virtualrepository.spi.MutableAsset;


public class PluginTests {
	
	Logger log = LoggerFactory.getLogger(RtmsBrowser.class);
	
	@BeforeClass
	public static void pluginLoads() {
		Services repos = new Services();
		repos.load();
		assertTrue(repos.size()>0);
	}
	
	@Test
	public void testFigisConnection() throws Exception	{
		 Connection conn = FigisConnectionManager.getInstance().getConnection();
		 Statement stmnt = conn.createStatement();
		 ResultSet rs = stmnt.executeQuery("SELECT COUNT(*) FROM FIGIS.MD_REFOBJECT");
		 rs.next();
		 int total = rs.getInt(1);
		 log.info("RTMS has {} meta concepts",total);
		 rs.close();
		 stmnt.close();
		 conn.close();
	}
	
	@Test
	public void testDiscoverCodelist() throws Exception	{
		RtmsCodelistFinder finder = new RtmsCodelistFinder();
		finder.discoverCodelists();
	}
	
	@Test
	public void testCodelist() throws Exception	{
		CodelistService service = CodelistServiceFactory.getService();
		RtmsCodelistFinder finder = new RtmsCodelistFinder();
		finder.discoverCodelists();
		Iterable<? extends MutableAsset> assets =  finder.getAssets((AbstractType<?>)CsvCodelist.type);
		int counter = 0;
		for (MutableAsset asset : assets) {
			log.info("Loading asset...{}",asset.id());
			service.getCodelist(asset);
			log.info("Table n.{}  loaded",counter);
			counter++;
			
		}
	}
	
}
