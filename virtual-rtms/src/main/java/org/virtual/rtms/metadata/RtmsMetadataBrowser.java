package org.virtual.rtms.metadata;

import static org.virtual.rtms.Constants.*;
import static org.virtual.rtms.Utils.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.RtmsConfiguration;
import org.virtualrepository.Properties;
import org.virtualrepository.Property;


public class RtmsMetadataBrowser {

	private static Logger log = LoggerFactory.getLogger(RtmsMetadataBrowser.class);
	
	private RtmsConfiguration configuration;
	
	public RtmsMetadataBrowser(RtmsConfiguration configuration) {
		this.configuration=configuration;
	}
	
	public RtmsMetadata discover() {
	
		log.info("discovering RTMS metadata...");
		
		long time = System.currentTimeMillis();
		
		Connection connection = null;
		
		try {
		
			connection =  configuration.connection();
			
			
			Map<Integer, RtmsInitialiser> initiators = discoverInitiators(connection);
			Map<Integer, RtmsConcept> concepts = discoverConcepts(connection,initiators);
			
			discoverAttributes(connection, concepts);
			postProcess(concepts);
			clean(concepts);
			
			log.info("discovered {} concepts in {} ms.",concepts.size(),System.currentTimeMillis()-time);
			
			return new RtmsMetadata(concepts, initiators);
		}
		catch(Exception e) {
			throw new RuntimeException("cannot discover RTMS metadata (see cause)",e);
		}
		finally {
			
			cleanup(connection);
		}
		
	}
	
	
	private Map<Integer, RtmsConcept> discoverConcepts(Connection connection,Map<Integer, RtmsInitialiser> classInits) throws Exception {
		
		Map<Integer,RtmsConcept> concepts = new LinkedHashMap<Integer,RtmsConcept>();

		Statement stmnt = connection.createStatement();
		ResultSet rs = stmnt.executeQuery(CONCEPTS_QUERY);
		
		while (rs.next()){
			
			int key = rs.getInt("ID");
			
			Properties properties = new Properties();
			
			addProp(properties,RTMS_CONCEPT_ID, key);
			addProp(properties,RTMS_CONCEPT_NAME_E, rs.getString("NAME_E"));
			addProp(properties,RTMS_CONCEPT_NAME_F, rs.getString("NAME_F"));
			addProp(properties,RTMS_CONCEPT_NAME_S, rs.getString("NAME_S"));
			addProp(properties,RTMS_CONCEPT_PARENT, rs.getInt("PARENT"));
			addProp(properties,RTMS_CONCEPT_ISMAJOR, rs.getInt("ISMAJOR"));
			
			RtmsConcept concept = new RtmsConcept(rs.getString("NAME_E"), properties);
			
			int classinit  = rs.getInt("READER_CLSINIT");
			
			if (classinit != 0) {
				addProp(properties,RTMS_CONCEPT_READERINIT, classinit);
				concept.setInitialiser(classInits.get(classinit));
			}
			
			if (rs.getString("DOC")!=null)  
				addProp(properties,RTMS_CONCEPT_DOC, rs.getString("DOC"));
			
			concepts.put(key, concept);
		}
		
		cleanup(stmnt, rs);
		
		return concepts;

	}


	//helper
	private Property prop(String name,Object value) {
		Property prop = new Property(name,value==null?"":value);	
		prop.display(false);
		return prop;
	}
	
	private void addProp(Properties props,String name,Object value) {
		if (value!=null)
			props.add(prop(name,value));
	}
	
	private void discoverAttributes(Connection connection, Map<Integer, RtmsConcept> concepts) throws Exception {
		
		Statement stmnt = connection.createStatement();
		ResultSet rs = stmnt.executeQuery(ATTRIBUTES_QUERY);
		
		while (rs.next()){
			
			int key = rs.getInt("ID");
			
			Properties properties = new Properties();
			
			addProp(properties,RTMS_ATTRIBUTE_ID, key);
			addProp(properties,RTMS_ATTRIBUTE_OBJID, rs.getInt("OBJID"));
			addProp(properties,RTMS_ATTRIBUTE_FLAGS, rs.getInt("FLAGS"));
			addProp(properties,RTMS_ATTRIBUTE_NAME_E, rs.getString("NAME_E"));
			addProp(properties,RTMS_ATTRIBUTE_NAME_F, rs.getString("NAME_F"));
			addProp(properties,RTMS_ATTRIBUTE_NAME_S, rs.getString("NAME_S"));
			addProp(properties,RTMS_ATTRIBUTE_CLASSNAME, rs.getString("CLASSNAME"));
			
			int init_id = rs.getInt("READER_CLSINIT");
			
			if (init_id !=0)
				addProp(properties,RTMS_ATTRIBUTE_READERINIT, init_id);

			
			RtmsAttribute attribute = new RtmsAttribute(configuration,rs.getString("NAME_E"),properties);
			
			//only possible heuristically so far
			attribute.isCode(attribute.name().toLowerCase().contains("code"));
			
			//attach attribute to corresponding concept
			RtmsConcept concept = concepts.get(attribute.conceptId());
			
			if (concept!=null)
				concept.addAttribute(attribute);
		}
		
		cleanup(stmnt, rs);
	}


	private Map<Integer, RtmsInitialiser> discoverInitiators(Connection connection) throws Exception {
		
		Map<Integer,RtmsInitialiser> classInits = new LinkedHashMap<Integer,RtmsInitialiser>();
		
		Statement stmnt = connection.createStatement();
		ResultSet rs = stmnt.executeQuery(CLASSINIT_QUERY);
		
		while (rs.next())
			classInits.put(rs.getInt("ID"), new RtmsInitialiser(rs.getString("INIT_XML")));
		
		cleanup(stmnt, rs);
		
		return classInits;
	}


	private void postProcess(Map<Integer, RtmsConcept> concepts) {
		
		for (RtmsConcept concept : concepts.values()) {
			
			int concept_id =  concept.properties().lookup(RTMS_CONCEPT_ID).value(Integer.class);
			int parent_id =   concept.properties().lookup(RTMS_CONCEPT_PARENT).value(Integer.class);
			
			while (parent_id > 0 || parent_id==concept_id) {
				
				RtmsConcept parentConcept = concepts.get(parent_id);
				concept.addAttributes(parentConcept.attributes());
				
				RtmsInitialiser parentInitialiser = parentConcept.initialiser();
				RtmsInitialiser initialiser = concept.initialiser();
				
				if (parentInitialiser!=null)
					if (initialiser==null)
						concept.setInitialiser(parentInitialiser);
					else 
						initialiser.integrate(parentInitialiser);
				
				parent_id =  parentConcept.properties().lookup(RTMS_CONCEPT_PARENT).value(Integer.class);
			}
		}
	}

	private void clean(Map<Integer, RtmsConcept> concepts) {
		
		List<Integer> toRemove = new LinkedList<Integer>();
		
		for (RtmsConcept concept : concepts.values())
			if (!concept.hasCodes() || concept.initialiser()==null)
				toRemove.add(concept.id());
		
		for (Integer id : toRemove)
			concepts.remove(id);
	}

}
