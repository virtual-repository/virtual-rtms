package org.virtual.rtms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.sdmxsource.sdmx.api.constants.STRUCTURE_OUTPUT_FORMAT;
import org.sdmxsource.sdmx.api.manager.output.StructureWriterManager;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.sdmxbeans.model.SdmxStructureFormat;
import org.sdmxsource.sdmx.util.beans.container.SdmxBeansImpl;
import org.virtualrepository.impl.Type;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.spi.Publisher;

@Singleton
public class SdmxPublisher implements Publisher<SdmxCodelist,CodelistBean> {

	private final BasePublisher publisher;
	private final StructureWriterManager writer;
	
	@Inject
	public SdmxPublisher(BasePublisher publisher,StructureWriterManager writer) {
		this.publisher=publisher;
		this.writer=writer;
	}
	
	@Override
	public Class<CodelistBean> api() {
		return CodelistBean.class;
	}
	
	@Override
	public Type<SdmxCodelist> type() {
		return SdmxCodelist.type;
	}
	
	public void publish(SdmxCodelist asset, CodelistBean content) throws Exception {
		
		InputStream stream = toStream(content);
		
		publisher.publish(asset, stream,"xml");
		
	};
	
	
	private InputStream toStream(CodelistBean content) {
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
		
		STRUCTURE_OUTPUT_FORMAT format = STRUCTURE_OUTPUT_FORMAT.SDMX_V21_STRUCTURE_DOCUMENT;
		
		writer.writeStructures(new SdmxBeansImpl(content), new SdmxStructureFormat(format), stream);
		
		return new ByteArrayInputStream(stream.toByteArray());
		
	}
	
}
