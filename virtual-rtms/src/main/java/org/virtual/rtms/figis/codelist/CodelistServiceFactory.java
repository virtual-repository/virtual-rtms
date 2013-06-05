/**
 * 
 */
package org.virtual.rtms.figis.codelist;

import org.virtual.rtms.figis.codelist.impl.CodelistServiceImpl;

/**
 * @author Sibeni
 *
 */
public class CodelistServiceFactory {

	/**
	 * The reference service singleton instance.
	 */
	private static CodelistService service;
	
	/**
	 * Return the reference service singleton instance.
	 * @return the reference service instance
	 */
	public static CodelistService getService() throws Exception {
		if (service == null) {
			service = new CodelistServiceImpl();
		}
		return service;
	}

}
