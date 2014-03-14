package org.virtual.rtms;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.virtual.rtms.utils.Table2SdmxTransform;
import org.virtualrepository.impl.Type;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.tabular.Table;

@Singleton
public class SdmxImporter implements Importer<SdmxCodelist,CodelistBean> {

	private final BaseImporter importer;

	@Inject
	public SdmxImporter(BaseImporter importer)  {
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
		
		Table2SdmxTransform transform = new Table2SdmxTransform();
		
		return transform.toSdmx(asset, table);
		
	}

}
