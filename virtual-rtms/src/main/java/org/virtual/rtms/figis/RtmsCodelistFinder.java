/**
 * 
 */
package org.virtual.rtms.figis;

import java.util.LinkedList;

import org.virtual.rtms.figis.impl.RtmsAbstractAttribute;
import org.virtual.rtms.figis.impl.RtmsAbstractConcept;
import org.virtualrepository.Property;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.AbstractType;
import org.virtualrepository.spi.MutableAsset;

/**
 * @author Fabrizio Sibeni
 *
 */
public class RtmsCodelistFinder {


	RtmsAbstractionService rtmsAbstractionService = null;



	public void discoverCodelists() throws Exception	{
		rtmsAbstractionService = RtmsAbstractionServiceFactory.getService();
	}







	public Iterable<? extends MutableAsset> getAssets(AbstractType<?> type) {

		if (type.equals(CsvCodelist.type)){
			return getCsvAssetList();
		}

		return null;
	}




	private LinkedList<CsvCodelist> getCsvAssetList() {
		LinkedList<CsvCodelist> res = new LinkedList<CsvCodelist>();
		for (RtmsAbstractConcept concept : rtmsAbstractionService.getConceptAbstractions()) {
			for (RtmsAbstractAttribute attribute : concept.codeAttributes()) {
				Property rtms_concept_property = concept.properties().lookup(RtmsAbstractConcept.RTMS_CONCEPT_ID);
				Property rtms_codeatt_property = attribute.properties().lookup(RtmsAbstractAttribute.RTMS_ATTRIBUTE_ID);
				String asset_id = "rtms" + rtms_concept_property.value() + "-" + rtms_codeatt_property.value();
				String asset_name = concept.name() + " - " + attribute.name();
				CsvCodelist asset = new CsvCodelist(asset_id, asset_name,0);
				asset.properties().add(rtms_concept_property);
				asset.properties().add(rtms_codeatt_property);
				
				for (Property property : asset.properties()) {
					property.display(false);
				}
				res.add(asset);
			} ;
		}
		return res;
	}




}
