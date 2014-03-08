package org.acme;

import static java.lang.Math.*;
import static org.acme.Utils.*;
import static org.virtual.rtms.RtmsConnection.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.inject.Inject;

import org.junit.Test;
import org.virtual.rtms.Dependencies;

import dagger.Module;

@Module(injects = Queries.class, includes = Dependencies.class)
public class Queries {

	@Inject
	Connection c;

	@Test
	public void tables() throws Exception {

		inject(this);

		try (

			ResultSet meta = c.getMetaData().getTables(null, "FIGIS",null, null);

		) 
		{
			while (meta.next())
				System.out.println(meta.getString("TABLE_NAME"));		
		}
	}
	
	
	@Test
	public void concepts() throws Exception {

		inject(this);
		
		String query = "select " 
				+"  C.PARENT"
				+", C.ID"
				+", C.NAME_E as NAME"
				+" from "
				+" FIGIS.MD_REFOBJECT C "
				+" where "
				+" C.ISMAJOR<2" //exclude filters
				+" order by PARENT"; 
		 

		sample(query);
		
		//265 in total, 190 excluding filters
		
	}
	
	
	
	@Test
	public void conceptsWithOptionalCodeAttributes() throws Exception {

		inject(this);
		
		String query = "select " 
				+"  C.PARENT"
				+", C.ID"
				+", C.NAME_E as NAME"
				+", CODELIST"
				+" from "
				+" FIGIS.MD_REFOBJECT C "
				+"    left join " 
			
						+"(SELECT NAME_E as CODELIST,OBJID "
						+"   from FIGIS.MD_REFATTR "
						+"   where (lower(NAME_E) like '%code%')) "
						+" on OBJID=C.ID" 
				
				+" where C.ISMAJOR<2" //exclude filters
				+" order by PARENT"; 
		 

		sample(query);
		
	}
	

	@Test
	public void codeAttributes() throws Exception {

		inject(this);

		String query = "select  " +
				"A.NAME_E, AI.INIT_XML "
				+" from  FIGIS.MD_REFATTR A,FIGIS.MD_CLASSINIT AI"
				+" where A.READER_CLSINIT=AI.ID and (A.NAME_E LIKE '%Code%' or A.NAME_E LIKE '%code%' or AI.INIT_XML LIKE '%CODE%')";
		
		sample(query);
	}
	
	
	@Test
	public void codes() throws Exception {

		inject(this);

		String query = "select A.ID as CODELIST_ID,A.NAME_E as CODELIST_NAME"
						+"			, A.OBJID "
						+"          , regexp_substr(I.INIT_XML,'(^|\\s)Column=([^;]+)') \"CODE_COLUMN\""
						+"          from FIGIS.MD_REFATTR A "
						+"          left outer join FIGIS.MD_CLASSINIT I on A.READER_CLSINIT=I.ID"
						+"          where (lower(A.NAME_E) like '%code%' or lower(I.INIT_XML) like '%column=%code%')"
						+"          order by A.OBJID"
						;
		
		sample(query);
		
		//53 codes
	}

	
	@Test
	public void codelists() throws Exception {

		inject(this);

		sample(forCodelists);
	}
	
	@Test
	public void codelist() throws Exception {
		
		inject(this);
		
		String query =  "select * from FIGIS.FIC_CATCH_AREA  order by FIC_SYS_CATCH_AREA";
		
		sample(query);

	}
	

	private void sample(String sql) throws Exception {

		try (	
				Statement stmnt = c.createStatement(); 
				ResultSet rs = stmnt.executeQuery(sql);
			) 
			{

			ResultSetMetaData md = rs.getMetaData();

			int i=0;
			while (rs.next()) {
				
				for (int j = 1; j <= md.getColumnCount(); j++) {
					
					String val = rs.getString(j);
					
					if (val != null)
						System.out.printf("%1$s = %2$s,  ", md.getColumnName(j), val.substring(0, min(val.length(),400)));
				}
				
				i++;
				
				System.out.println();
			}
			
			System.out.println("\ntotal="+i);
		}

	}
}
