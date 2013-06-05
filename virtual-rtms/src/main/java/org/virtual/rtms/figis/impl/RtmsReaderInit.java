/**
 * 
 */
package org.virtual.rtms.figis.impl;

import java.util.Hashtable;


/**
 * @author Sibeni
 *
 */
public class RtmsReaderInit  {

	
	public static final String DEFAULT_COLUMN_META = "META";
	
	public static final String INIT_TABLE = "Table";
	public static final String INIT_COLUMN = "Column";
	public static final String INIT_META_COLUMN = "MetaColumn";
	
	
	
	
	private int id;
	
	private String classname;
	
	private Hashtable<String, String> inits = new Hashtable<String, String>();
	
	
	/**
	 * 
	 */
	public RtmsReaderInit(int id, String classname, String init) {
		this.id = id;
		this.classname = classname;
		parseInitString(init);
	}


	public int getId() {
		return id;
	}


	public String getClassname() {
		return classname;
	}

	public boolean hasInit(String key){
		return inits.containsKey(key);
	}

	public String getInit(String key){
		return inits.get(key);
	}

	
	protected void integrate (RtmsReaderInit rtmsReaderInit){
		for (String key : rtmsReaderInit.inits.keySet()) {
			if (!inits.containsKey(key)){
				inits.put(key, rtmsReaderInit.inits.get(key));
			}
		} 
	}


	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append("inits: ");
		for (String key : inits.keySet()) {
			res.append(key);
			res.append(": " );
			res.append(inits.get(key));
			res.append(" - ");
		}
		res.delete(res.length()-3, res.length());
		return res.toString();
	}
	

	private void parseInitString(String initString) {
		if (initString==null) return;
		String[] entries =  initString.split(";");
		for (String entry : entries) {
			entry = entry.trim();
			String[] couple = entry.split("=");
			if (!inits.containsKey(couple[0])){
				inits.put(couple[0], couple[1]);
			}
		}
	}
	
	
	
	


	
	
}
