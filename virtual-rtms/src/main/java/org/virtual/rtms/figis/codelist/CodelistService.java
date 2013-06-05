/**
 * 
 */
package org.virtual.rtms.figis.codelist;

import org.virtualrepository.spi.MutableAsset;
import org.virtualrepository.tabular.Table;

/**
 * The reference data service.
 * @author Fabrizio Sibeni
 *
 */
public interface CodelistService {

	/**
	 * Return a codelist via its acronym.
	 * @param acronym the concept acronym
	 * @return the requested concept if existing, null otherwise 
	 * @throws ReferenceServiceException
	 */
	public Table getCodelist(MutableAsset asset) throws Exception;
	
}
