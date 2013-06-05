/**
 * 
 */
package org.virtual.rtms.figis.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.figis.FigisConnectionManager;
import org.virtual.rtms.figis.RtmsAbstractionService;
import org.virtual.rtms.figis.RtmsCodelistFinder;
import org.virtualrepository.Property;

/**
 * @author Sibeni
 *
 */
public class RtmsAbstractionServiceImpl implements RtmsAbstractionService {

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(RtmsCodelistFinder.class);

	private Connection connection = null;

	private static final int INITIAL_CAPACITY = 400;


	private static final String CONCEPTS_QUERY = "SELECT * FROM FIGIS.MD_REFOBJECT WHERE MD_REFOBJECT.ISMAJOR < 2  AND MD_REFOBJECT.ID > 0 ORDER BY MD_REFOBJECT.ID";
	private static final String ATTRIBUTES_QUERY = "SELECT * FROM FIGIS.MD_REFATTR ORDER BY MD_REFATTR.OBJID,MD_REFATTR.ID";
	private static final String CLASSINIT_QUERY = "SELECT * FROM FIGIS.MD_CLASSINIT";



	LinkedHashMap<Integer, RtmsAbstractConcept> conceptsList;
	LinkedHashMap<Integer, RtmsReaderInit> classInitsList;



	/**
	 * 
	 */
	public RtmsAbstractionServiceImpl() throws Exception {
		connection =  FigisConnectionManager.getInstance().getConnection();
		gatherClassInits();
		gatherConcepts();
		gatherAttributes();
		postProcessConcepts();
		cleanConceptsList();
		connection.close();// TODO Auto-generated constructor stub
	}




	@Override
	public Collection<RtmsAbstractConcept> getConceptAbstractions() {
		return conceptsList.values();
	}

	@Override
	public RtmsAbstractConcept getConceptAbstraction(Integer id) {
		return conceptsList.get(id) ;
	}


	


	@Override
	public boolean hasConceptAbstraction(Integer id) {
		return conceptsList.containsKey(id);
	}




	@Override
	public boolean hasAttribute(Integer concept_id, Integer attribute_id) throws Exception {
		if (! conceptsList.containsKey(concept_id)) throw new Exception("Invalid concept id");
		RtmsAbstractConcept rtmsAbstractConcept = conceptsList.get(concept_id);
		return rtmsAbstractConcept.hasAttribute(attribute_id);
	}


	
	
	


	@Override
	public boolean hasReader(Integer id) {
		return classInitsList.containsKey(id);
	}




	@Override
	public RtmsReaderInit getReader(Integer id) throws Exception {
		if (!classInitsList.containsKey(id)) throw new Exception("Reader not found: " + id);
		return classInitsList.get(id);
	}




	private void gatherConcepts() throws Exception {
		conceptsList = new LinkedHashMap<Integer, RtmsAbstractConcept>(INITIAL_CAPACITY);


		Statement stmnt = connection.createStatement();
		ResultSet rs = stmnt.executeQuery(CONCEPTS_QUERY);
		while (rs.next()){
			int key = rs.getInt("ID");
			LinkedList<Property> properties = new LinkedList<Property>();
			properties.add(new Property(RtmsAbstractConcept.RTMS_CONCEPT_ID, key));
			properties.add(new Property(RtmsAbstractConcept.RTMS_CONCEPT_NAME_E, rs.getString("NAME_E")!=null?rs.getString("NAME_E"):""));
			properties.add(new Property(RtmsAbstractConcept.RTMS_CONCEPT_NAME_F, rs.getString("NAME_F")!=null?rs.getString("NAME_F"):""));
			properties.add(new Property(RtmsAbstractConcept.RTMS_CONCEPT_NAME_S, rs.getString("NAME_S")!=null?rs.getString("NAME_S"):""));
			properties.add(new Property(RtmsAbstractConcept.RTMS_CONCEPT_PARENT, rs.getInt("PARENT")));
			properties.add(new Property(RtmsAbstractConcept.RTMS_CONCEPT_ISMAJOR, rs.getInt("ISMAJOR")));
			Integer classinit  = rs.getInt("READER_CLSINIT");
			if (classinit != null){
				properties.add(new Property(RtmsAbstractConcept.RTMS_CONCEPT_READERINIT, classinit));
			} 
			if (rs.getString("DOC")!=null)  properties.add(new Property(RtmsAbstractConcept.RTMS_CONCEPT_DOC, rs.getString("DOC")));
			for (Property property : properties) {
				property.display(false);
			}
			RtmsAbstractConcept concept = new RtmsAbstractConcept(RtmsAbstractConcept.type, Integer.toString(key), rs.getString("NAME_E"), properties.toArray(new Property[]{}));
			concept.initiator = classinit!=null? classInitsList.get(classinit):null;
			conceptsList.put(key, concept);
		}
		rs.close();
		stmnt.close();

	}


	private void gatherAttributes() throws Exception {
		Statement stmnt = connection.createStatement();
		ResultSet rs = stmnt.executeQuery(ATTRIBUTES_QUERY);
		while (rs.next()){
			int key = rs.getInt("ID");
			LinkedList<Property> properties = new LinkedList<Property>();
			properties.add(new Property(RtmsAbstractAttribute.RTMS_ATTRIBUTE_ID, key));
			properties.add(new Property(RtmsAbstractAttribute.RTMS_ATTRIBUTE_OBJID, rs.getInt("OBJID")));
			properties.add(new Property(RtmsAbstractAttribute.RTMS_ATTRIBUTE_FLAGS, rs.getInt("FLAGS")));
			properties.add(new Property(RtmsAbstractAttribute.RTMS_ATTRIBUTE_NAME_E, rs.getString("NAME_E")!=null?rs.getString("NAME_E"):""));
			properties.add(new Property(RtmsAbstractAttribute.RTMS_ATTRIBUTE_NAME_F, rs.getString("NAME_F")!=null?rs.getString("NAME_F"):""));
			properties.add(new Property(RtmsAbstractAttribute.RTMS_ATTRIBUTE_NAME_S, rs.getString("NAME_S")!=null?rs.getString("NAME_S"):""));
			properties.add(new Property(RtmsAbstractAttribute.RTMS_ATTRIBUTE_CLASSNAME, rs.getString("CLASSNAME")));
			Integer init_id = rs.getInt("READER_CLSINIT");
			if (init_id != null){
				properties.add(new Property(RtmsAbstractAttribute.RTMS_ATTRIBUTE_READERINIT, init_id));
			}
			for (Property property : properties) {
				property.display(false);
			}
			RtmsAbstractAttribute abstractAttribute = new RtmsAbstractAttribute(RtmsAbstractAttribute.type, Integer.toString(key), rs.getString("NAME_E"), properties.toArray(new Property[]{}));
			processAttribute(abstractAttribute);
		}
		rs.close();
		stmnt.close();
	}


	private void gatherClassInits() throws Exception {
		classInitsList = new LinkedHashMap<Integer, RtmsReaderInit>(INITIAL_CAPACITY);
		Statement stmnt = connection.createStatement();
		ResultSet rs = stmnt.executeQuery(CLASSINIT_QUERY);
		while (rs.next()){
			int id = rs.getInt("ID");
			classInitsList.put(id, new RtmsReaderInit(id, rs.getString("CLASSNAME"), rs.getString("INIT_XML")));
		}
		rs.close();
		stmnt.close();
	}


	private void processAttribute(RtmsAbstractAttribute attribute) {
		
		RtmsAbstractConcept concept = conceptsList.get(attribute.properties().lookup(RtmsAbstractAttribute.RTMS_ATTRIBUTE_OBJID).value());
		if (concept!=null){
			concept.addAttribute(attribute);
		}
	}


	private void postProcessConcepts() {
		for (RtmsAbstractConcept concept : conceptsList.values()) {
			Integer concept_id =  (Integer) concept.properties().lookup(RtmsAbstractConcept.RTMS_CONCEPT_ID).value();
			//log.info("Processing concept[{}]...", concept_id);
			Integer parent_id =  (Integer) concept.properties().lookup(RtmsAbstractConcept.RTMS_CONCEPT_PARENT).value();
			while (parent_id > 0 || parent_id==concept_id) {
				//log.info("   ...on parent[{}]...", parent_id);
				RtmsAbstractConcept parentConcept = conceptsList.get(parent_id);
				concept.addAttributes(parentConcept.getAttributes());
				if (parentConcept.initiator!=null)	{
					if (concept.initiator==null){
						concept.initiator = parentConcept.initiator;
					} else {
						concept.initiator.integrate(parentConcept.initiator);
					}
				}
				parent_id =  (Integer) parentConcept.properties().lookup(RtmsAbstractConcept.RTMS_CONCEPT_PARENT).value();
			}
		}
	}

	private void cleanConceptsList() {
		List<Integer> toRemove = new LinkedList<Integer>();
		for (RtmsAbstractConcept concept : conceptsList.values()) {
			if (!concept.hasCodelists() || concept.initiator==null){
				toRemove.add((Integer)concept.properties().lookup(RtmsAbstractConcept.RTMS_CONCEPT_ID).value());
			}
		}
		for (Integer id : toRemove) {
			conceptsList.remove(id);
		}
	}





}
