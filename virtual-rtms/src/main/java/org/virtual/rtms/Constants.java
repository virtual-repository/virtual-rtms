package org.virtual.rtms;


public class Constants {
	
	public static final String CONFIG_ENDPOINT = "figis.endpoint";
	public static final String CONFIG_DRIVER = "figis.driver";
	public static final String CONFIG_URL = "figis.url";
	public static final String CONFIG_USER = "figis.user";
	public static final String CONFIG_PWD = "figis.pwd";
	public static final String CONFIG_NO_REFRESH = "figis.norefresh";
	
	
	public static final String CONCEPTS_QUERY = "SELECT * FROM FIGIS.MD_REFOBJECT WHERE MD_REFOBJECT.ISMAJOR < 2  AND MD_REFOBJECT.ID > 0 ORDER BY MD_REFOBJECT.ID";
	
	public static final String ATTRIBUTES_QUERY = "SELECT * FROM FIGIS.MD_REFATTR ORDER BY MD_REFATTR.OBJID,MD_REFATTR.ID";
	
	public static final String CLASSINIT_QUERY = "SELECT * FROM FIGIS.MD_CLASSINIT";
	
	public final static String RTMS_ATTRIBUTE_ID = "rtms.attribute.id";
	
	public final static String RTMS_ATTRIBUTE_OBJID = "rtms.attribute.objid";
	
	public final static String RTMS_ATTRIBUTE_FLAGS = "rtms.attribute.flags";
	
	public final static String RTMS_ATTRIBUTE_NAME_E = "rtms.attribute.name_e";
	
	public final static String RTMS_ATTRIBUTE_NAME_F = "rtms.attribute.name_f";
	
	public final static String RTMS_ATTRIBUTE_NAME_S = "rtms.attribute.name_s";
	
	public final static String RTMS_ATTRIBUTE_CLASSNAME = "rtms.attribute.classname";
	
	public final static String RTMS_ATTRIBUTE_READERINIT = "rtms.attribute.readerinit";
	
	public final static String RTMS_CONCEPT_ID = "rtms.concept.id";
	
	public final static String RTMS_CONCEPT_NAME_E = "rtms.concept.name_e";
	
	public final static String RTMS_CONCEPT_NAME_F = "rtms.concept.name_f";
	
	public final static String RTMS_CONCEPT_NAME_S = "rtms.concept.name_s";
	
	public final static String RTMS_CONCEPT_PARENT = "rtms.concept.parent";
	
	public final static String RTMS_CONCEPT_ISMAJOR = "rtms.concept.ismajor";
	
	public final static String RTMS_CONCEPT_DOC = "rtms.concept.doc";
	
	public final static String RTMS_CONCEPT_READERINIT = "rtms.concept.readerinit";
	
	final public static String META_ATTR_NAME = "org.fao.fi.figis.refservice.metadata.attr.MetaAttrName";

	final public static String META_ATTR_STRING = "org.fao.fi.figis.refservice.metadata.attr.MetaAttrString";
	
	public static final String DEFAULT_COLUMN_META = "META";

	
	public static final String INIT_TABLE = "Table";
	public static final String INIT_COLUMN = "Column";
	public static final String INIT_META_COLUMN = "MetaColumn";
}
