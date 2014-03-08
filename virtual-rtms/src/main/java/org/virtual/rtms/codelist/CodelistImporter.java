/**
 * 
 */
package org.virtual.rtms.codelist;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.Rtms;
import org.virtual.rtms.RtmsConnection;
import org.virtualrepository.Asset;
import org.virtualrepository.tabular.Table;


/**
 * Imports codelists in an internal model.
 * <p>
 * It is wrapped to convert the model into specific asset types.
 */
public class CodelistImporter {

	private static final Logger log = LoggerFactory.getLogger(CodelistImporter.class);
	
	private final Rtms rtms;

	@Inject
	public CodelistImporter(Rtms rtms) {
		this.rtms=rtms;
	}
	
	public Table retrieve(final Asset asset) throws Exception {
		
		log.info("retrieving asset "+asset.id());
		
		
		try (RtmsConnection connection = rtms.connect())
		{
			return connection.retrieve(asset);
		}

	}
}
