package org.virtual.rtms.codelist;

import org.virtual.rtms.RtmsConfiguration;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Type;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.tabular.Table;

public class CsvCodelistImporter implements Importer<CsvCodelist, Table> {

	CodelistImporter codelistService;
	
	
	public CsvCodelistImporter(RtmsConfiguration configuration) throws Exception  {
		codelistService = new CodelistImporter(configuration);
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
		return codelistService.retrieveCodelistFrom(asset);
	}

}
