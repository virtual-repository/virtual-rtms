/**
 * 
 */
package org.virtual.rtms.figis.codelist.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.virtual.rtms.figis.FigisConnectionManager;
import org.virtual.rtms.figis.RtmsAbstractionService;
import org.virtual.rtms.figis.RtmsAbstractionServiceFactory;
import org.virtual.rtms.figis.impl.RtmsAbstractAttribute;
import org.virtual.rtms.figis.impl.RtmsAbstractConcept;
import org.virtual.rtms.figis.impl.RtmsReaderInit;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.Row;

/**
 * @author Sibeni
 *
 */
public class CodelistAccessorImpl {

	final public static String META_ATTR_NAME = "org.fao.fi.figis.refservice.metadata.attr.MetaAttrName";
	final public static String META_ATTR_STRING = "org.fao.fi.figis.refservice.metadata.attr.MetaAttrString";


	private Connection connection = null;

	private RtmsAbstractConcept concept;


	private List<RtmsAbstractAttribute> attributes;

	private RtmsAbstractionService rtmsAbstractionService;

	private Statement stmnt;

	private ResultSet rs;

	private List<Column> columns;

	private RtmsAbstractAttribute name_attribute = null;


	public CodelistAccessorImpl(Integer concept_id, Integer... attribute_ids) throws Exception {
		super();
		rtmsAbstractionService = RtmsAbstractionServiceFactory.getService();
		this.concept = rtmsAbstractionService.getConceptAbstraction(concept_id);

		attributes = new LinkedList<RtmsAbstractAttribute>();
		for (Integer attribute_id : attribute_ids) {
			attributes.add(concept.getAttribute(attribute_id));
		}
		connection =  FigisConnectionManager.getInstance().getConnection();
		name_attribute =  concept.getAttribute(1);
		
		columns = defineColumns();
	}




	private String buildCodelistQuery() throws Exception {
		StringBuffer res = new StringBuffer(200);
		res.append("SELECT ");
		for (int a = 0; a < attributes.size() ; a++) {
			RtmsAbstractAttribute attribute = attributes.get(a);
			res.append(getAttributeColumn(attribute));
			res.append(", ");
		}

		String name_column = name_attribute.getInit().getInit(RtmsReaderInit.INIT_COLUMN);
		
		if (name_attribute.properties().lookup(RtmsAbstractAttribute.RTMS_ATTRIBUTE_CLASSNAME).value(String.class).equals(META_ATTR_NAME)){
			res.append(name_column + "_E, ");
			res.append(name_column + "_F, ");
			res.append(name_column + "_S ");

		} else {
			res.append(name_column + " ");
		}

		res.append("FROM FIGIS.");
		res.append(concept.getInitiator().getInit(RtmsReaderInit.INIT_TABLE));
		res.append(" WHERE ");
		res.append(concept.getInitiator().hasInit(RtmsReaderInit.INIT_META_COLUMN)?concept.getInitiator().getInit(RtmsReaderInit.INIT_META_COLUMN):RtmsReaderInit.DEFAULT_COLUMN_META);
		res.append(" = ");
		res.append(concept.getFigisId());
		res.append(" ORDER BY ");
		res.append(getAttributeColumn(attributes.get(0)));
		return res.toString();
	}




	private String getAttributeColumn(RtmsAbstractAttribute attribute) throws Exception {
		RtmsReaderInit reader_init = attribute.getInit();
		String res =  reader_init.getInit(RtmsReaderInit.INIT_COLUMN);
		return res;
	}



	public void  loadCodelist() throws Exception {
		stmnt = connection.createStatement();
		rs = stmnt.executeQuery(buildCodelistQuery());
	}


	public void disposeAccessor() throws Exception {
		rs.close();
		stmnt.close();
		connection.close();
	}



	@Override
	public String toString() {

		try {
			return " SELECT QUERY: " + buildCodelistQuery();
		} catch (Exception e) {
			return ("invalid accessor. Cause: " + e.getMessage());
		}

	}




	public List<Column> getColumns() {
		return columns;
	}




	private List<Column> defineColumns(){
		List<Column> columns = new LinkedList<Column>();
		for (int a = 0; a < attributes.size() ; a++) {
			columns.add(new Column(attributes.get(a).name()));
		}
		if (name_attribute.properties().lookup(RtmsAbstractAttribute.RTMS_ATTRIBUTE_CLASSNAME).value(String.class).equals(META_ATTR_NAME)){
			columns.add(new Column("Name (en)"));
			columns.add(new Column("Name (fr)"));
			columns.add(new Column("Name (es)"));
		} else {
			columns.add(new Column("Name"));
		}


		return columns;
	}


	protected Row getCurrentRow() throws Exception {
		Map<QName, String> data = new Hashtable<QName, String>(columns.size());
		for (int i = 0; i < columns.size(); i++) {

			data.put(columns.get(i).name(), rs.getString(i+1)!=null? rs.getString(i+1):"");
		}
		return new Row(data);
	}


	protected boolean nextRow() throws Exception {
		return rs.next();
	}



}
