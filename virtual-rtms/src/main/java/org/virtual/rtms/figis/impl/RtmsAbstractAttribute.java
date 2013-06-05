/**
 * 
 */
package org.virtual.rtms.figis.impl;

import org.virtual.rtms.figis.RtmsAbstractionServiceFactory;
import org.virtualrepository.AssetType;
import org.virtualrepository.Property;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.AbstractAsset;
import org.virtualrepository.impl.AbstractType;
import org.virtualrepository.impl.Type;

/**
 * @author Fabrizio Sibeni
 *
 */
public class RtmsAbstractAttribute extends AbstractAsset {

	public final static String RTMS_ATTRIBUTE_ID = "rtms.attribute.id";
	
	public final static String RTMS_ATTRIBUTE_OBJID = "rtms.attribute.objid";
	
	public final static String RTMS_ATTRIBUTE_FLAGS = "rtms.attribute.flags";
	
	public final static String RTMS_ATTRIBUTE_NAME_E = "rtms.attribute.name_e";
	
	public final static String RTMS_ATTRIBUTE_NAME_F = "rtms.attribute.name_f";
	
	public final static String RTMS_ATTRIBUTE_NAME_S = "rtms.attribute.name_s";
	
	public final static String RTMS_ATTRIBUTE_CLASSNAME = "rtms.attribute.classname";
	
	public final static String RTMS_ATTRIBUTE_READERINIT = "rtms.attribute.readerinit";
	
	
	private static final String name = "rtms/attribute";
	
	@SuppressWarnings("unused")
	private RtmsReaderInit init = null;
	

	/**
	 * The type of {@link CsvCodelist}s.
	 */
	public static final Type<RtmsAbstractAttribute> type = new AbstractType<RtmsAbstractAttribute>(name) {};
	
	public RtmsAbstractAttribute(AssetType type, String id, String name,	Property... properties) {
		super(type, id, name, properties);
	}

	public RtmsReaderInit getInit() throws Exception {
		return RtmsAbstractionServiceFactory.getService().getReader(properties().lookup(RTMS_ATTRIBUTE_READERINIT).value(Integer.class));
		
		
		
	}
	
	
	
	
	
	
	
}
