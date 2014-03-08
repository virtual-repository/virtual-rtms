package org.virtual.rtms;

import static java.lang.System.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.rtms.model.Codelist;
import org.virtualrepository.AssetType;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.spi.Browser;
import org.virtualrepository.spi.MutableAsset;

@Singleton
public class RtmsBrowser implements Browser {

	private static Logger log = LoggerFactory.getLogger(RtmsBrowser.class);

	
	private final Rtms rtms;
	private final Configuration configuration;
	
	private Collection<Codelist> cached;
	
	
	@Inject
	public RtmsBrowser(Rtms rtms, Configuration configuration) {
		this.rtms = rtms;
		this.configuration = configuration;
	}
	
	@Override
	public Iterable<? extends MutableAsset> discover(Collection<? extends AssetType> types) throws Exception {

		Iterable<Codelist> codelists = discover();
		
		if (types.contains(CsvCodelist.type))
			return toCsvAssets(codelists);
		
		if (types.contains((SdmxCodelist.type)))
			return toSdmxAssets(codelists);
		
		throw new IllegalArgumentException("unsupported types "+types);
		
	}

	
		
	private Iterable<CsvCodelist> toCsvAssets(Iterable<Codelist> codelists) {
	
		log.info("discovering {}",CsvCodelist.type);
		
		List<CsvCodelist> assets = new ArrayList<CsvCodelist>();
		
		for (Codelist codelist : codelists)
			assets.add(codelist.toCsvAsset());
		
		return assets;
	
	}
	
	
	private List<SdmxCodelist> toSdmxAssets(Iterable<Codelist> codelists) {
		
		log.info(" discovering {}",SdmxCodelist.type);

		List<SdmxCodelist> assets = new ArrayList<SdmxCodelist>();
		
		for (Codelist codelist : codelists)
			assets.add(codelist.toSdmxAsset());
		
		return assets;
	}
	

	private Iterable<Codelist> discover() {

		// we've done it once and do not need to do it again
		if (cached != null && configuration.noRefresh())
			return cached;
		
		
		log.info("discovering rtms codelists...");

		try (

			RtmsConnection conn = rtms.connect()

		)

		{

			long time = currentTimeMillis();

			cached = conn.codelists();

			log.info("found {} codelists in rtms in {} ms.", cached.size(), currentTimeMillis() - time);
			
			return cached;

		}

		catch (Exception e) {

			throw new RuntimeException("cannot discover rtms codelists (see cause)", e);
		}
	}

	

}
