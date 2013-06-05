/**
 * 
 */
package org.virtual.rtms.figis;

import java.util.Collection;

import org.virtual.rtms.figis.impl.RtmsAbstractConcept;
import org.virtual.rtms.figis.impl.RtmsReaderInit;

/**
 * @author Sibeni
 *
 */
public interface RtmsAbstractionService {

	
	public Collection<RtmsAbstractConcept> getConceptAbstractions();

	public RtmsAbstractConcept getConceptAbstraction(Integer id);
	
	public boolean  hasConceptAbstraction(Integer id);
	public boolean  hasAttribute (Integer concept_id, Integer attribute_id) throws Exception;
	
	public boolean  hasReader(Integer id);
	
	public RtmsReaderInit  getReader(Integer id)  throws Exception ;

	
	
}
