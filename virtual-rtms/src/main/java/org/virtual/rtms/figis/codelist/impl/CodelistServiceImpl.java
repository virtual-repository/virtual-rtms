/**
 * 
 */
package org.virtual.rtms.figis.codelist.impl;

import java.util.LinkedList;

import org.virtual.rtms.figis.RtmsAbstractionService;
import org.virtual.rtms.figis.RtmsAbstractionServiceFactory;
import org.virtual.rtms.figis.codelist.CodelistService;
import org.virtual.rtms.figis.impl.RtmsAbstractAttribute;
import org.virtual.rtms.figis.impl.RtmsAbstractConcept;
import org.virtualrepository.Property;
import org.virtualrepository.spi.MutableAsset;
import org.virtualrepository.tabular.DefaultTable;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

/**
 * @author Fabrizio Sibeni
 *
 */
public class CodelistServiceImpl implements CodelistService {

	RtmsAbstractionService rtmsAbstractionService = null;

	
	/**
	 * 
	 */
	public CodelistServiceImpl() throws Exception {
		rtmsAbstractionService = RtmsAbstractionServiceFactory.getService();
		
	}
	
	
	

	@Override
	public Table getCodelist(MutableAsset asset) throws Exception {
		
		Property rtms_concept_property = asset.properties().lookup(RtmsAbstractConcept.RTMS_CONCEPT_ID);
		if (rtms_concept_property==null) {
			throw new Exception("Missing property: " );
		}
		Integer concept_id = rtms_concept_property.value(Integer.class);
		
		Property rtms_codeatt_property = asset.properties().lookup(RtmsAbstractAttribute.RTMS_ATTRIBUTE_ID);
		if (rtms_codeatt_property==null) {
			throw new Exception("Missing property: " );
		}
		Integer attribute_id = rtms_codeatt_property.value(Integer.class);
		
		if (!rtmsAbstractionService.hasAttribute(concept_id, attribute_id)) throw new Exception("Invalid attribute code");
		System.out.print(asset.id() + ": ");
		CodelistAccessorImpl codelistAccessor = new CodelistAccessorImpl(concept_id, attribute_id);
		codelistAccessor.loadCodelist();
		LinkedList<Row> rows = new LinkedList<Row>();
		while (codelistAccessor.nextRow()) {
			rows.add(codelistAccessor.getCurrentRow());
		}
		DefaultTable res = new DefaultTable(codelistAccessor.getColumns(), rows);
		codelistAccessor.disposeAccessor();
		return res;
	}

	
	
	
	
	
}
