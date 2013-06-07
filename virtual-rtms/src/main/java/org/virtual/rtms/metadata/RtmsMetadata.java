package org.virtual.rtms.metadata;

import java.util.Collection;
import java.util.Map;


public class RtmsMetadata {

	private final Map<Integer, RtmsConcept> concepts;
	private final Map<Integer, RtmsInitialiser> initialisers;
	
	public RtmsMetadata(Map<Integer,RtmsConcept> concepts, Map<Integer, RtmsInitialiser> initialisers) {
		this.concepts=concepts;
		this.initialisers=initialisers;
	}
	
	public Collection<RtmsConcept> concepts() {
		return concepts.values();
	}

	public RtmsConcept concept(Integer id) {
		return concepts.get(id) ;
	}

	public RtmsInitialiser initialiser(Integer id) {
		if (!initialisers.containsKey(id)) 
			throw new IllegalStateException("unknown initialiser: " + id);
		return initialisers.get(id);
	}

}
