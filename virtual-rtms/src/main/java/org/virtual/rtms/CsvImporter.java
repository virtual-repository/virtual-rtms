package org.virtual.rtms;

import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Type;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.tabular.Table;

public class CsvImporter implements Importer<CsvCodelist, Table> {

	@SuppressWarnings("unused")
	private final RtmsConfiguration configuration;

	public CsvImporter(RtmsConfiguration configuration) {
		this.configuration = configuration;
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
		// TODO Auto-generated method stub
		return null;
	}

}
