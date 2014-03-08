package org.virtual.rtms.codelist;

import javax.inject.Inject;

import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.virtualrepository.impl.Type;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.tabular.Table;

public class SdmxCodelistImporter implements Importer<SdmxCodelist,CodelistBean> {

	private final CodelistImporter importer;

	@Inject
	public SdmxCodelistImporter(CodelistImporter importer)  {
		this.importer = importer;
	}
	
	@Override
	public Type<SdmxCodelist> type() {
		return SdmxCodelist.type;
	}

	@Override
	public Class<CodelistBean> api() {
		return CodelistBean.class;
	}

	@Override
	public CodelistBean retrieve(SdmxCodelist asset) throws Exception {
		
		Table table = importer.retrieve(asset);
		
		Table2SdmxCodelist transform = new Table2SdmxCodelist();
		
		return transform.toSdmx(asset, table);
		
	}

}
