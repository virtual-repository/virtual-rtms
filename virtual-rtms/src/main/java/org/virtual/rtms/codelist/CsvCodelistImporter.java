package org.virtual.rtms.codelist;

import javax.inject.Inject;

import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Type;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.Table;

public class CsvCodelistImporter implements Importer<CsvCodelist, Table> {

	CodelistImporter importer;
	
	
	@Inject
	public CsvCodelistImporter(CodelistImporter importer)  {
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
