package org.virtual.rtms;

import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Type;
import org.virtualrepository.spi.Publisher;
import org.virtualrepository.tabular.Table;

public class CsvPublisher implements Publisher<CsvCodelist,Table> {

	@SuppressWarnings("unused")
	private final RtmsConfiguration configuration;

	public CsvPublisher(RtmsConfiguration configuration) {
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
	public void publish(CsvCodelist asset, Table content) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
