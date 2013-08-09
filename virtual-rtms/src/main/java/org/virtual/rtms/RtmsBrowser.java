package org.virtual.rtms;

import static org.virtual.rtms.Constants.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.metadata.RtmsAttribute;
import org.virtual.rtms.metadata.RtmsConcept;
import org.virtual.rtms.metadata.RtmsMetadata;
import org.virtualrepository.AssetType;
import org.virtualrepository.Property;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.spi.Browser;
import org.virtualrepository.spi.MutableAsset;

public class RtmsBrowser implements Browser {

	private final RtmsConfiguration configuration;

	private static Logger log = LoggerFactory.getLogger(RtmsBrowser.class);
	
	public RtmsBrowser(RtmsConfiguration configuration) throws Exception {
		this.configuration = configuration;
	}
	
	@Override
	public Iterable<? extends MutableAsset> discover(Collection<? extends AssetType> types) throws Exception {

		configuration.refresh();
		
		RtmsMetadata metadata = configuration.metadata();
		
		//return tabular data as a preference to avoid an unsupervised mapping
		if (types.contains(CsvCodelist.type))
			return discoverCsvCodelists(metadata);
		
		//coding cautiously below: VR should not pass us an unsupported type
		
		//if sdmx is all client takes.. 
		if (types.contains((SdmxCodelist.type)))
			return discoverSdmxCodelists(metadata);
		
		throw new IllegalArgumentException("unsupported types "+types);
		
	}

	
	
	
	private List<CsvCodelist> discoverCsvCodelists(RtmsMetadata metadata) {
	
		log.info(" discovering {}",CsvCodelist.type);
		
		LinkedList<CsvCodelist> assets = new LinkedList<CsvCodelist>();
		
		for (RtmsConcept concept : metadata.concepts())
			for (RtmsAttribute attribute : concept.codeAttributes()) {
				Property conceptId = concept.properties().lookup(RTMS_CONCEPT_ID);
				Property attributeId = attribute.properties().lookup(RTMS_ATTRIBUTE_ID);
				String assetId = "rtms-" + conceptId.value() + "-" + attributeId.value();
				String assetName = concept.name() + " - " + attribute.name();
				CsvCodelist asset = new CsvCodelist(assetId, assetName,0);
				asset.properties().add(conceptId);
				asset.properties().add(attributeId);
				
				assets.add(asset);
			}
		
		return assets;
		
	}
	
	
	private List<SdmxCodelist> discoverSdmxCodelists(RtmsMetadata metadata) {
		
		log.info(" discovering {}",SdmxCodelist.type);

		List<SdmxCodelist> assets = new ArrayList<SdmxCodelist>();
		
		for (RtmsConcept concept : metadata.concepts())
			for (RtmsAttribute attribute : concept.codeAttributes()) {
				Property conceptId = concept.properties().lookup(RTMS_CONCEPT_ID);
				Property attributeId = attribute.properties().lookup(RTMS_ATTRIBUTE_ID);
				String assetId = "rtms-" + conceptId.value() + "-" + attributeId.value();
				String assetName = concept.name() + " - " + attribute.name();
				SdmxCodelist asset = new SdmxCodelist(assetId+"-sdmx", assetId, "1.0",assetName);
				asset.properties().add(conceptId);
				asset.properties().add(attributeId);
				
				assets.add(asset);
			}
		
		return assets;
	}
}
