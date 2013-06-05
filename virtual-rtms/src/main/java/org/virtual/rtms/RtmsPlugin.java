package org.virtual.rtms;

import static java.util.Collections.singleton;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.virtualrepository.RepositoryService;
import org.virtualrepository.spi.Plugin;

public class RtmsPlugin implements Plugin {

	public static QName name = new QName("RTMS");
	
	
	@Override
	public Collection<RepositoryService> services() {
		return singleton(new RepositoryService(name, new RtmsProxy()));
	}


}
