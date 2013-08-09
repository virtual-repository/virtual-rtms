package org.virtual.rtms.codelist;

import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.virtual.rtms.RtmsConfiguration;
import org.virtualrepository.impl.Type;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.tabular.Table;

public class SdmxCodelistImporter implements Importer<SdmxCodelist,CodelistBean> {

	private final CodelistImporter codelistService;

	public SdmxCodelistImporter(RtmsConfiguration configuration) throws Exception  {
		this.codelistService = new CodelistImporter(configuration);
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
		
		Table table = codelistService.retrieveCodelistFrom(asset);
		
		Table2SdmxCodelist transform = new Table2SdmxCodelist();
		
		return transform.toSdmx(asset, table);
		
	}

}
