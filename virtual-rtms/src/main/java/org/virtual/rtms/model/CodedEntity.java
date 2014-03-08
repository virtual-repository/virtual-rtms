package org.virtual.rtms.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodedEntity extends Entity {

	public final static String parent_id_prop = "parent id";

	private boolean isParent=false;
	private final Map<String,CodeAttribute> attributes = new HashMap<>();

	public CodedEntity(String pid, String id, String name, String table, String column) {
		
		super(id,name,table,column);
		set(parent_id_prop,pid);

	}
	

	public String parentId() {
		return get(parent_id_prop);
	}

	public void setParent() {
		isParent=true;
	}
	
	public boolean isParent() {
		return isParent;
	}
	
	public Collection<CodeAttribute> attributes() {
		
		return attributes.values();
	}
	
	public CodedEntity attribute(String id, String name, String column) {
		
		if (id!=null)
			attribute(new CodeAttribute(id,name,table(),column));
		
		return this;
	}
	
	public CodedEntity attribute(CodeAttribute code) {
		
		if (!attributes.containsKey(code.id()))
			attributes.put(code.id(),code);
		
		return this;
	}
	
	public List<Codelist> codelists() {
		
		List<Codelist> lists = new ArrayList<>();
		
		//some entities are abstract but have codes to propagate down
		if (!isAbstract())
			for (CodeAttribute attribute : attributes.values())
				lists.add(new Codelist(this,attribute));
		
		return lists;
	}
	
	@Override
	public String toString() {
		return super.toString()+", pid="+parentId()+", attributes"+attributes.values();
	}
}
