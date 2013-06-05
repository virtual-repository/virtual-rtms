/**
 * 
 */
package org.virtual.rtms.figis;

import org.virtual.rtms.figis.impl.RtmsAbstractionServiceImpl;

/**
 * @author Sibeni
 *
 */
public class RtmsAbstractionServiceFactory {

	/**
	 * The reference service singleton instance.
	 */
	private static RtmsAbstractionService service;
	
	/**
	 * Return the reference service singleton instance.
	 * @return the reference service instance
	 */
	public static RtmsAbstractionService getService() throws  Exception {
		if (service == null) {
			service = new RtmsAbstractionServiceImpl();
		}
		return service;
	}

}
