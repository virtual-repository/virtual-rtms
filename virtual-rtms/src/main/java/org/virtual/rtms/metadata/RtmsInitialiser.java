/**
 * 
 */
package org.virtual.rtms.metadata;

import java.util.Hashtable;

public class RtmsInitialiser  {

	private Hashtable<String, String> inits = new Hashtable<String, String>();
	
	
	public RtmsInitialiser(String init) {
		parseInitString(init);
	}

	public boolean hasInit(String key){
		return inits.containsKey(key);
	}

	public String init(String key){
		return inits.get(key);
	}

	
	protected void integrate(RtmsInitialiser rtmsReaderInit){
		for (String key : rtmsReaderInit.inits.keySet())
			if (!inits.containsKey(key))
				inits.put(key, rtmsReaderInit.inits.get(key));
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
