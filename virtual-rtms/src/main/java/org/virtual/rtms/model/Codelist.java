package org.virtual.rtms.model;

import static java.lang.String.*;

import org.virtualrepository.Asset;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.sdmx.SdmxCodelist;

public class Codelist extends Entity {


	public final static String entity_col_prop = "meta";
	public final static String query_prop = "prop";
	
	public Codelist(CodedEntity entity, CodeAttribute code) {
		
		
		super(compose(entity.id(),code.id()),
			  compose(entity.name(),code.name()),
			  entity.table(), 
			  code.column());

		String cond = entity.column() == null || entity.isParent() ? "" : format("where %1$s=%2$s",entity.column(),entity.id());
		
		String query = format("select * from FIGIS.%1$s %2$s order by %3$s", entity.table(), cond, column());
		
		set(query_prop,query,false);
		set(entity_col_prop,entity.column(),false);
	}
	
	public Codelist(Asset asset) {
		super(asset.properties());
	}
	
	public String entityColumn() {
		return get(entity_col_prop);
	}
	
	
	
	public String query() {
		return get(query_prop);
	}
	
	public SdmxCodelist toSdmxAsset() {

		return new SdmxCodelist(id()+ "-sdmx", id(), "unknown", name(), properties().toArray());

	}

	public CsvCodelist toCsvAsset() {

		return new CsvCodelist(id(), name(), 0, properties().toArray());

	}
	
	@Override
	public String toString() {
		return super.toString()+", query=\""+query()+"\"";
	}
	
	private static String compose(String id1, String id2) {
		return format("rtms-%s:%s", id1, id2);
	}
}
