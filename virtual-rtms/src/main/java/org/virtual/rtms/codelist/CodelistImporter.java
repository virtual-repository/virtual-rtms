/**
 * 
 */
package org.virtual.rtms.codelist;

import static java.util.Collections.singletonList;
import static org.virtual.rtms.Constants.DEFAULT_COLUMN_META;
import static org.virtual.rtms.Constants.INIT_COLUMN;
import static org.virtual.rtms.Constants.INIT_META_COLUMN;
import static org.virtual.rtms.Constants.INIT_TABLE;
import static org.virtual.rtms.Constants.META_ATTR_NAME;
import static org.virtual.rtms.Constants.RTMS_ATTRIBUTE_CLASSNAME;
import static org.virtual.rtms.Constants.RTMS_ATTRIBUTE_ID;
import static org.virtual.rtms.Constants.RTMS_CONCEPT_ID;
import static org.virtual.rtms.Utils.cleanup;
import static org.virtualrepository.Utils.notNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.RtmsConfiguration;
import org.virtual.rtms.metadata.RtmsAttribute;
import org.virtual.rtms.metadata.RtmsConcept;
import org.virtual.rtms.metadata.RtmsInitialiser;
import org.virtualrepository.Asset;
import org.virtualrepository.Property;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.DefaultTable;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

/**
 * @author Fabrizio Sibeni
 *
 */
//table-based importer, asset-agnostic
public class CodelistImporter {

	private static final Logger log = LoggerFactory.getLogger(CodelistImporter.class);
	
	private final RtmsConfiguration configuration;
	
	public CodelistImporter(RtmsConfiguration configuration) throws Exception {
		this.configuration=configuration;
		
	}
	
	private RtmsConcept extractCodeConceptFromAsset(Asset asset) {

		Property conceptProperty = asset.properties().lookup(RTMS_CONCEPT_ID);
		
		if (conceptProperty==null)
			throw new IllegalArgumentException("missing asset property "+RTMS_CONCEPT_ID);
		
		Integer conceptId = conceptProperty.value(Integer.class);
		
		RtmsConcept concept = configuration.metadata().concept(conceptId);
		
		if (concept==null)
			throw new IllegalArgumentException("unknown concept "+conceptId);
		
		return concept;
	}
	
	private RtmsAttribute extractCodeAttributeFromAsset(RtmsConcept concept, Asset asset) {

		Property attributeProperty = asset.properties().lookup(RTMS_ATTRIBUTE_ID);
		
		if (attributeProperty==null) 
			throw new IllegalArgumentException("missing asset property "+RTMS_ATTRIBUTE_ID);
		
		Integer attributeId = attributeProperty.value(Integer.class);
		
		RtmsAttribute attribute = concept.attribute(attributeId);
		
		if (attribute==null)
			throw new IllegalArgumentException("unknown attribute code "+attributeId);
		
		return attribute;
	}
	
	private Set<String> columnNames(Connection connection, RtmsConcept concept) throws SQLException {
		Set<String> columnNames = new HashSet<String>();
		
		ResultSet meta = connection.getMetaData().getColumns(null, "FIGIS", concept.initialiser().init(INIT_TABLE), null);
		
		while(meta.next()) {
			columnNames.add(meta.getString("COLUMN_NAME"));
		}
		
		meta.close();
		
		return columnNames;
	}
	
	public Table retrieveCodelistFrom(Asset asset) {
		
		log.info("retrieving asset "+asset.id());
		
		long time = System.currentTimeMillis();
		
		try {
			
			RtmsConcept concept = extractCodeConceptFromAsset(asset);
			RtmsAttribute attribute = extractCodeAttributeFromAsset(concept,asset);
			
			RtmsAttribute nameAttribute = concept.attribute(1);
	
			//for now, just code attribute
			List<RtmsAttribute> attributes = singletonList(attribute);
			//columns are currently based on name attribute
			List<Column> columns = defineColumns(nameAttribute,attributes);

			//Helps removing the assumption that the code column is always the first... 
			int codeIndex = 0;
			
			for(Column current : columns) {
				if(current.name().getLocalPart().equals(attribute.name()))
					break;
				else
					codeIndex++;
			}
			
			//Here, codeIndex will point to the column that has a name matching the code attribute for the asset
			
			LinkedList<Row> rows = new LinkedList<Row>();
			
			Connection connection = null;
			Statement stmnt = null;
			ResultSet rs = null;
			try {
				connection = configuration.connection();
				

				stmnt = connection.createStatement();
				String query = buildQuery(concept,nameAttribute,attributes, this.columnNames(connection, concept));
				
				try {
					rs = stmnt.executeQuery(query);
				} catch(SQLException SQLe) {
					log.error("Unable to execute query {} ({})", query, SQLe.getMessage());
					
					throw new RuntimeException("Unable to execute query", SQLe);
				}
				
				while (rs.next()) {
					Row row = nextRow(rs,columns);
					//add it only if it has a code
					if (row.get(columns.get(codeIndex))!=null)
						rows.add(row);
				}
				
				log.info("retrieved asset {} in {} ms.",asset.id(),System.currentTimeMillis()-time);
				
				return new DefaultTable(columns, rows);
				
	
			}
			finally {
				cleanup(connection, stmnt, rs);
			}
		}

		catch(Exception e) {
			throw new RuntimeException("cannot retrieve codelist (see cause)",e);
		}
	}
	
	
	private String buildQuery(RtmsConcept concept, RtmsAttribute name_attribute, List<RtmsAttribute> attributes, Set<String> columnNames) {
		List<String> columns = new ArrayList<String>();

		for (RtmsAttribute attribute : attributes) {
			if(columnNames.contains(getAttributeColumn(attribute)))
				columns.add(getAttributeColumn(attribute));
		}

		String name_column = name_attribute.initialiser().init(INIT_COLUMN);
		
		if(!columnNames.contains(name_column + "_E"))
			if(columnNames.contains("LONG_NAME_E") || columnNames.contains("LONG_NAME"))
				name_column = "LONG_NAME";
			else if(columnNames.contains("NAME_E") || columnNames.contains("NAME"))
				name_column = "NAME";
			else if(columnNames.contains("SHORT_NAME_E") || columnNames.contains("SHORT_NAME"))
				name_column = "SHORT_NAME";
			else
				;//FAQ OFF!!!

		if(name_attribute.properties().lookup(RTMS_ATTRIBUTE_CLASSNAME).value(String.class).equals(META_ATTR_NAME)){
			if(columnNames.contains(name_column + "_E")) {
				columns.add(name_column + "_E");

				if(columnNames.contains(name_column + "_F"))
					columns.add(name_column + "_F");
				else 
					columns.add("NULL AS " + name_column + "_F");

				if(columnNames.contains(name_column + "_S"))
					columns.add(name_column + "_S");
				else 
					columns.add("NULL AS " + name_column + "_S");
			} else if(columnNames.contains(name_column)) {
				columns.add(name_column + " AS " + name_column + "_E");
				columns.add("NULL AS " + name_column + "_F");
				columns.add("NULL AS " + name_column + "_S");
			}
		} else
			columns.add(name_column);

		if(columns.isEmpty())
			throw new RuntimeException("No columns to select from " + concept.initialiser().init(INIT_TABLE));
		
		StringBuffer res = new StringBuffer(200);
		res.append("SELECT ");

		if(!columns.isEmpty())
			res.append(this.join(", ", columns.toArray(new String[columns.size()]))).append(" ");
		
		res.append("FROM FIGIS.");
		res.append(concept.initialiser().init(INIT_TABLE));
		res.append(" WHERE ");
		res.append(concept.initialiser().hasInit(INIT_META_COLUMN)?concept.initialiser().init(INIT_META_COLUMN):DEFAULT_COLUMN_META);
		res.append(" = ");
		res.append(concept.id());
		res.append(" ORDER BY ");
		res.append(getAttributeColumn(attributes.get(0)));
		
		return res.toString();
	}
	
	//helpers
	
	private String getAttributeColumn(RtmsAttribute attribute) {
		RtmsInitialiser reader_init = attribute.initialiser();
		return reader_init.init(INIT_COLUMN);
	}
	
	private Row nextRow(ResultSet rs,List<Column> columns) throws Exception {
		Map<QName, String> data = new Hashtable<QName, String>(columns.size());

		for (int i = 0; i < columns.size(); i++) {
			if (rs.getString(i+1)!=null)
				data.put(columns.get(i).name(), rs.getString(i+1));
		}
		
		Row row = new Row(data);
		
		return row;
	}
	
	private List<Column> defineColumns(RtmsAttribute nameAttribute, List<RtmsAttribute> attributes){
		
		List<Column> columns = new LinkedList<Column>();
		
		for (RtmsAttribute attribute : attributes)
			columns.add(new Column(attribute.name()));

		if (nameAttribute.properties().lookup(RTMS_ATTRIBUTE_CLASSNAME).value(String.class).equals(META_ATTR_NAME)){
			columns.add(new Column("Name (en)"));
			columns.add(new Column("Name (fr)"));
			columns.add(new Column("Name (es)"));
		} 
		else 
			columns.add(new Column("Name"));

		return columns;
	}

	private String join(String separator, String... toJoin) {
		notNull(separator, "Separator cannot be null");
		
		if(toJoin == null || toJoin.length == 0)
			return "";
		
		StringBuilder result = new StringBuilder();
		for(String part : toJoin)
			result.append(part).append(separator);
		
		String asString = result.toString();
		
		return asString.substring(0, asString.lastIndexOf(separator));
	}
}
