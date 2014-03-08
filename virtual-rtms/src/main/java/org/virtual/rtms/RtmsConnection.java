package org.virtual.rtms;

import static java.lang.System.*;
import static org.virtualrepository.Utils.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.model.CodeAttribute;
import org.virtual.rtms.model.CodedEntity;
import org.virtual.rtms.model.Codelist;
import org.virtualrepository.Asset;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.DefaultTable;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

/**
 * Single point of integration with repository.
 * 
 */
public class RtmsConnection implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(RtmsConnection.class);

	
	//column labels as found in result sets
	private static final String rs_parent = "PARENT";
	private static final String rs_id = "ID";
	private static final String rs_name = "NAME";
	private static final String rs_meta_column="META_COLUMN";
	
	private static final String rs_codelist_name = "CODELIST_NAME";
	private static final String rs_codelist_id = "CODELIST_ID";

	private static final String rs_table="TABLE";
	private static final String rs_code_column="CODE_COLUMN";
	
	//defaults
	private static final String default_meta_column="META";

	
	
	//name-based heuristic to recognise code attribute
	private static final String codeHeuristics = "(lower(A.NAME_E) like '%code%' or lower(AI.INIT_XML) like '%column=%code%')";
	
	//inner query: returns information about code attributes (id,name,entity, and column)
	public static final String codeAttributes = "select A.ID as CODELIST_ID,A.NAME_E as CODELIST_NAME"
			+"			, A.OBJID as OWNER "
			+"          , regexp_substr(AI.INIT_XML,'(^|\\s)Column=([^;]+)') \"CODE_COLUMN\""
			+"          from FIGIS.MD_REFATTR A "
			+"          left outer join FIGIS.MD_CLASSINIT AI on A.READER_CLSINIT=AI.ID"
			+"          where "+codeHeuristics
			;
	
	//main query: returns entities with their id, name, parent and whatever is available between table, filter column (meta), and code attributes
	//can get back:
	// abstract entities (no table info) with or without code info to propagate to children
	// pure children with table info but no direct codes
	// anything in between really
	public static final String forCodelists = "select  "
	
						+"	C.PARENT"
						+", C.ID" 
						+", C.NAME_E as NAME"
						+", CODELIST_ID"
						+", CODELIST_NAME"
						+", CODE_COLUMN"
						+", regexp_substr(I.INIT_XML,'(^|\\s)Table=([^;]+)') \"TABLE\""
						+", regexp_substr(I.INIT_XML,'MetaColumn=([^;]+)') \"META_COLUMN\"" 
						
						+" from "
						+"  FIGIS.MD_REFOBJECT C"
						
						+"    left join ("+codeAttributes+") on OWNER=C.ID"
						
						+"    left join "
						+"       FIGIS.MD_CLASSINIT I on I.ID=C.READER_CLSINIT"
						
						+" where C.ID>0 and ISMAJOR<2"; //ISMJAOR==2 is a 'filter' entity in RTMS
			
					

	
	private final Connection connection;
	private final PreparedStatement stmt;

	@Inject
	public RtmsConnection(Connection connection) {

		notNull("connection", connection);

		this.connection = connection;
		
		try {
			this.stmt = connection.prepareStatement(forCodelists);
			this.stmt.setFetchSize(200); //we roughly know what to expect and do not expect it to change
			this.stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
		}
		catch(Exception e) {
			throw new AssertionError("error in query statement (see cause)",e);
		}
	}
	
	
	
	public Collection<Codelist> codelists() throws Exception {
		
		try (
				
			ResultSet results = stmt.executeQuery();
		) 
		{
	
			//pull results
			Map<String,CodedEntity> entities = entitiesFrom(results);
			
			//push coded attributes down
			Collection<CodedEntity> resolved = resolveInheritanceOn(entities);
			
			//get codelists for each entity (one per coded attribute)
			List<Codelist> codelists = new ArrayList<>();
			
			for (CodedEntity e : resolved)
				codelists.addAll(e.codelists());

			return codelists;
		}

		
	}
	
	private Collection<CodedEntity> resolveInheritanceOn(Map<String,CodedEntity> entities) {
		
		Map<String,Boolean> globallyVisited = new HashMap<>();
		
		for (CodedEntity e : entities.values())
			inheritOn(e,entities,globallyVisited);
		
		return entities.values();
	}
	
	private Map<String,CodedEntity> entitiesFrom(ResultSet rs) throws Exception {
		
		Map<String,CodedEntity> entities = new HashMap<>();
		
		while (rs.next()) {
			
			CodedEntity next = nextEntity(rs); 
		
			//merge codes on existing entities, or add entity for the first time
			if (entities.containsKey(next.id())) {
				CodedEntity e = entities.get(next.id());
				for (CodeAttribute code : next.attributes())
						e.attribute(code);
			}
			else
				entities.put(next.id(),next);
		}
		
		return entities;

	}
	
	
	private void inheritOn(CodedEntity e, Map<String,CodedEntity> entities, Map<String,Boolean> visited) {
		
		//remember to avoid visiting this twice
		visited.put(e.id(),true);
		
		CodedEntity parent = null;
		
		//watch out for corner cases or erroneous data that would send us in a loop
		parent = entities.get(e.parentId());
		
		//nothing to inherit
		if (parent==null)
			return;
		
		//prepare parent first, unless previously done
		if (visited.containsKey(parent.id()))
			parent.setParent();
		else
			inheritOn(parent,entities,visited);
		
		//copy parent code attributes
		for (CodeAttribute a : parent.attributes())
			e.attribute(a);

		if (e.column()==null)
	
			if (parent.column()==null)
				if (!parent.isAbstract()) 
					e.column(default_meta_column);
	
			else
				e.column(parent.column());
			

	}
	
	private CodedEntity nextEntity(ResultSet rs) throws Exception {

		String parent = rs.getString(rs_parent);
		String id = rs.getString(rs_id);
		String name = rs.getString(rs_name);
		String table = rs.getString(rs_table)==null?null:rs.getString(rs_table).split("=")[1];
		String col = rs.getString(rs_meta_column)==null?null:rs.getString(rs_meta_column).split("=")[1]; 
		
		CodedEntity entity = new CodedEntity(parent,id,name,table,col);

		id = rs.getString(rs_codelist_id);
		name = rs.getString(rs_codelist_name);
		col = rs.getString(rs_code_column)==null?null:rs.getString(rs_code_column).split("=")[1]; 
		
		entity.attribute(id, name, col);

		return entity;
		
	}

	
	
	
	public Table retrieve(Asset asset) throws Exception {
		
		Codelist codelist = codelistFor(asset);
		
		String query = codelist.query();
		
		long time = currentTimeMillis();
		
		log.trace("retrieving {} with {}",codelist.name(),query);
		
		try (
			Statement stmnt = statement(1000);
			ResultSet rs = stmnt.executeQuery(query);
		)
		{
			log.trace("rtms returned results for {} in {} ms",codelist.name(),System.currentTimeMillis()-time);

			Column code = new Column(codelist.column());
			
			List<Column> columns = columns(rs,codelist,code);
			
			List<Row> rows = new ArrayList<>();
			
			int count=0;
			
			while (rs.next()) {
				
				Row row = nextRow(rs,columns);
				
				 //add only valid data (with code)
				if (row.get(code) != null) {
					rows.add(row);
			        count++;
				}
			}
			
			log.trace("retrieved {} for {} in {} ms",count,codelist.name(),currentTimeMillis()-time);
			
			return new DefaultTable(columns, rows);
		}
		
	}

	@Override
	public void close() throws Exception {

		connection.close();

	}

	//helpers
	private Row nextRow(ResultSet rs,List<Column> columns) throws Exception {
		Map<QName, String> data = new Hashtable<QName, String>(columns.size());

		for (int i = 0; i < columns.size(); i++)
			if (rs.getString(i+1)!=null)
				data.put(columns.get(i).name(), rs.getString(i+1));
		
		Row row = new Row(data);
		
		return row;
	}
	
	private Statement statement(int fetchSize) throws Exception {
		Statement stmnt = connection.createStatement();
		stmnt.setFetchSize(fetchSize);
		stmnt.setFetchDirection(ResultSet.FETCH_FORWARD);
		return stmnt;
	}
	
	private Codelist codelistFor(Asset asset) {

		Codelist codelist = new Codelist(asset);
		
		if (codelist.query()==null)
			throw new IllegalArgumentException("malformed asset: properties required to retrieve codelist are missing");

		return codelist;
	}
	
	
	private List<Column> columns(ResultSet rs,Codelist codelist, Column codeColumn) throws Exception {
		
		List<Column> columns = new ArrayList<>();
		
		String code = codelist.column();
		String meta = codelist.entityColumn();
		
		columns.add(codeColumn);

		ResultSetMetaData metadata = rs.getMetaData();
		
		for (int i=1; i<=metadata.getColumnCount(); i++) {
		
			String name = metadata.getColumnName(i);
			
			//do not add code twice, and exclude meta column if any
			if (!code.equals(name) && (meta==null || !meta.equals(name))) 
				columns.add(new Column(name));
		}
		
		return columns;
	}
}
