package org.virtual.rtms;

import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.csv.Table2CsvStream;
import org.virtualrepository.impl.Type;
import org.virtualrepository.spi.Publisher;
import org.virtualrepository.tabular.Table;

@Singleton
public class CsvPublisher implements Publisher<CsvCodelist,Table> {

	private final BasePublisher publisher;
	
	@Inject
	public CsvPublisher(BasePublisher publisher) {
		this.publisher=publisher;
	}
	
	@Override
	public Class<Table> api() {
		return Table.class;
	}
	
	@Override
	public Type<CsvCodelist> type() {
		return CsvCodelist.type;
	}
	
	public void publish(CsvCodelist asset, Table content) throws Exception {
		
		InputStream stream = new Table2CsvStream<>().apply(asset,content);
		
		publisher.publish(asset, stream,"txt");
		
	};
	
}
