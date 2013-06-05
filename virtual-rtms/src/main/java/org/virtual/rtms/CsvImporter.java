package org.virtual.rtms;

import org.virtual.rtms.figis.codelist.CodelistService;
import org.virtual.rtms.figis.codelist.CodelistServiceFactory;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Type;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.tabular.Table;

public class CsvImporter implements Importer<CsvCodelist, Table> {

	CodelistService codelistService;
	
	
	@SuppressWarnings("unused")
	private final RtmsConfiguration configuration;

	public CsvImporter(RtmsConfiguration configuration) throws Exception  {
		this.configuration = configuration;
		codelistService = CodelistServiceFactory.getService();
	}
	
	@Override
	public Type<CsvCodelist> type() {
		return CsvCodelist.type;
	}

	@Override
	public Class<Table> api() {
		return Table.class;
	}

	@Override
	public Table retrieve(CsvCodelist asset) throws Exception {
		Table res = null;
		res = codelistService.getCodelist(asset);
		return res;
	}

}
