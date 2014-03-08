package org.virtual.rtms;

import static java.util.Collections.*;

import java.util.Collection;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.virtualrepository.Property;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.spi.Plugin;

import dagger.Module;
import dagger.ObjectGraph;

/**
 * Provides an access service to one given Rtms installation.
 * 
 */
@Module(injects=RtmsPlugin.class,includes=Dependencies.class)
public class RtmsPlugin implements Plugin {

	public static QName name = new QName("rtms");

	public static String about="The Reference Tables Management System (RTMS) is the main reference database " +
			"in the Fisheries and Acquaculture Department of the Food and Agriculture Organisation (FAO) of the United" +
			"Nations.";
	
	@Inject
	RtmsProxy proxy;
	
	@Inject
	Configuration configuration;
	

	
	@Override
	public Collection<RepositoryService> services() {
		
		ObjectGraph.create(this,Dependencies.instance).inject(this);
		
		RepositoryService service = new RepositoryService(name,proxy,properties());
		
		return singleton(service);
	}
	
	private Property[] properties() {
			
			Property blurb = new Property("RTMS",about);
			Property location = new Property("location",configuration.endpoint());
			
			return new Property[]{blurb,location};
	}

	

}
