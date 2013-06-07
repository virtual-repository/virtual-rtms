/**
 * 
 */
package org.virtual.rtms.metadata;

import static org.virtual.rtms.Constants.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.virtualrepository.Properties;

/**
 * 
 * A concept has an identifier and attributes, some of which identifies codes.
 * 
 * @author Fabrizio Sibeni
 *
 */
public class RtmsConcept extends RtmsMetadataElement {

	private LinkedHashMap<Integer, RtmsAttribute> attributes = new LinkedHashMap<Integer, RtmsAttribute>();

	private LinkedList<RtmsAttribute> codeAttributes = new LinkedList<RtmsAttribute>();

	private RtmsInitialiser initialisers;
	
	public RtmsConcept(String name, Properties properties) {
		super(name, properties);	
	}

	public int id(){
		return properties().lookup(RTMS_CONCEPT_ID).value(Integer.class);
	}

	public Collection<RtmsAttribute> attributes() {
		return attributes.values();
	}

	public void addAttribute(RtmsAttribute attribute) {
		
		attributes.put(attribute.id(),attribute);
		
		if (attribute.isCode() && !codeAttributes.contains(attribute))
			codeAttributes.add(attribute);

	}

	public void addAttributes(Collection<RtmsAttribute> attributes) {
		for (RtmsAttribute attribute : attributes)
			addAttribute(attribute);
	}

	public boolean hasCodes(){
		return codeAttributes.size()>0;
	}

	public Iterable<RtmsAttribute> codeAttributes() {
		return codeAttributes;
	}
	
	public RtmsAttribute attribute(Integer id) {
		
		if (!attributes.containsKey(id)) 
			throw new IllegalStateException("unknown attribute : "  + id);
		
		return attributes.get(id);
		
	}
	
	
	public RtmsInitialiser initialiser() {
		return initialisers;
	}
	
	public void setInitialiser(RtmsInitialiser initialiser) {
		this.initialisers=initialiser;
	}
	

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer(1000);
		res.append("RtmsConcept ").append(id());
		
		if (initialisers!=null)
			res.append(" - ").append(initialisers);

		res.append(" [");
		for (RtmsAttribute attribute : attributes.values())
			res.append(attribute).append(" - ");
		res.append("]");
		
		return res.toString();
	}




}
