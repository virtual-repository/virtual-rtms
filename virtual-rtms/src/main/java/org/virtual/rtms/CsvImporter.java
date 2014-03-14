package org.virtual.rtms;

import javax.inject.Inject;

import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Type;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.Table;

public class CsvImporter implements Importer<CsvCodelist, Table> {

	BaseImporter importer;
	
	
	@Inject
	public CsvImporter(BaseImporter importer)  {
		this.importer = importer;
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
		
		Table table = importer.retrieve(asset); 
		
		asset.setColumns(table.columns().toArray(new Column[0]));
		
		return table;
	}

}
