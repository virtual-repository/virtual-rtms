package org.virtual.rtms;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.figis.RtmsCodelistFinder;
import org.virtualrepository.AssetType;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.AbstractType;
import org.virtualrepository.spi.Browser;
import org.virtualrepository.spi.MutableAsset;

public class RtmsBrowser implements Browser {

	Logger log = LoggerFactory.getLogger(RtmsBrowser.class);
	
	@Override
	public Iterable<? extends MutableAsset> discover(Collection<? extends AssetType> types) throws Exception {

		for(AssetType type : types) {
			
			if (type==CsvCodelist.type)
				return discoverCsvCodelist();
			else
				throw new IllegalArgumentException("unsupported type "+type);
			
		}
		
		throw new IllegalArgumentException("invoked with no types "); 
		
	}

	
	
	
	private Iterable<? extends MutableAsset> discoverCsvCodelist() {
	
		log.info(" discovering {}",CsvCodelist.type);
		RtmsCodelistFinder finder = new RtmsCodelistFinder();
		
		try {
			finder.discoverCodelists();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return finder.getAssets((AbstractType<?>) CsvCodelist.type) ;
		
	}

}
