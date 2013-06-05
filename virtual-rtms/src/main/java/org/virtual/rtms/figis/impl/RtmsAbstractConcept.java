/**
 * 
 */
package org.virtual.rtms.figis.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.virtualrepository.AssetType;
import org.virtualrepository.Property;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.AbstractAsset;
import org.virtualrepository.impl.AbstractType;
import org.virtualrepository.impl.Type;

/**
 * @author Fabrizio Sibeni
 *
 */
public class RtmsAbstractConcept extends AbstractAsset {

	private static final String name = "rtms/concept";

	/**
	 * RTMS concept identifier column
	 */
	public final static String RTMS_CONCEPT_ID = "rtms.concept.id";
	
	/**
	 * RTMS concept english name column
	 */
	public final static String RTMS_CONCEPT_NAME_E = "rtms.concept.name_e";
	
	/**
	 * RTMS concept french name column
	 */
	public final static String RTMS_CONCEPT_NAME_F = "rtms.concept.name_f";
	
	public final static String RTMS_CONCEPT_NAME_S = "rtms.concept.name_s";
	
	public final static String RTMS_CONCEPT_PARENT = "rtms.concept.parent";
	
	public final static String RTMS_CONCEPT_ISMAJOR = "rtms.concept.ismajor";
	
	public final static String RTMS_CONCEPT_DOC = "rtms.concept.doc";
	
	public final static String RTMS_CONCEPT_READERINIT = "rtms.concept.readerinit";
	
	
	
	
	
	private LinkedHashMap<Integer, RtmsAbstractAttribute> attributes = new LinkedHashMap<Integer, RtmsAbstractAttribute>();

	private LinkedList<RtmsAbstractAttribute> codeAttributes = new LinkedList<RtmsAbstractAttribute>();

	protected RtmsReaderInit initiator;
	
	

	/**
	 * The type of {@link CsvCodelist}s.
	 */
	public static final Type<RtmsAbstractConcept> type = new AbstractType<RtmsAbstractConcept>(name) {};

	public RtmsAbstractConcept(AssetType type, String id, String name,	Property... properties) {
		super(type, id, name, properties);
	}


	public Collection<RtmsAbstractAttribute> getAttributes() {
		return attributes.values();
	}

	public void addAttribute(RtmsAbstractAttribute attribute) {
		Integer attribute_id = (Integer)attribute.properties().lookup(RtmsAbstractAttribute.RTMS_ATTRIBUTE_ID).value();
		if (attributes.containsKey(attribute_id)){
			return;
		} else {
			attributes.put(attribute_id, attribute);
		}

		if (attribute.name().toLowerCase().contains("code")) {
			if (!codeAttributes.contains(attribute)) {
				codeAttributes.add(attribute);
			}
			attribute.properties().add(new Property("iscode", true));
		}

	}

	public void addAttributes(Collection<RtmsAbstractAttribute> attributes) {
		for (RtmsAbstractAttribute attribute : attributes) {
			addAttribute(attribute);
		}
	}

	public boolean hasCodelists(){
		return codeAttributes.size()>0;
	}

	public Iterable<RtmsAbstractAttribute> codeAttributes() {
		return codeAttributes;
	}


	public Integer getFigisId(){
		return properties().lookup(RTMS_CONCEPT_ID).value(Integer.class);
	}
	
	

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer(1000);
		res.append("RtmsAbstractConcept ");
		res.append(properties().lookup(RTMS_CONCEPT_ID).value());
		
		if (properties().contains(RTMS_CONCEPT_READERINIT)){
			res.append(" - ");
			res.append(properties().lookup(RTMS_CONCEPT_READERINIT).value(RtmsReaderInit.class).toString());
		}

		res.append(" [");
		for (RtmsAbstractAttribute attribute : attributes.values()) {
			if (attribute.properties().contains("iscode")){
				res.append('*');
			}
			res.append(attribute.name());
			res.append(" - ");
		}
		res.append("]");
		return res.toString();
	}


	public boolean hasAttribute(Integer attribute_id) {
		return attributes.containsKey(attribute_id);
	}

	public RtmsAbstractAttribute getAttribute(Integer attribute_id) throws Exception {
		if (!attributes.containsKey(attribute_id)) throw new Exception("Attribute not found: "  + attribute_id);
		return attributes.get(attribute_id);
		
	}


	public RtmsReaderInit getInitiator() {
		return initiator;
	}



}
