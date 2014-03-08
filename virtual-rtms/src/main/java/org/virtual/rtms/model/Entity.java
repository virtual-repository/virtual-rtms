package org.virtual.rtms.model;

import static java.lang.String.*;

import org.virtualrepository.Properties;
import org.virtualrepository.Property;

public class Entity {

	public final static String id_prop = "id";
	public final static String name_prop = "name";
	
	public final static String table_prop = "table";
	public final static String column_prop = "col";
	
	private final Properties properties;
	
	public Entity() {
		this(new Properties());
	}
	
	public Entity(Properties properties) {
		this.properties=properties;
	}
	
	public Entity(String id, String name, String table, String column) {
		
		this();
		
		set(id_prop,id);
		set(name_prop,name);
		set(table_prop,table);
		
		column(column);

	}
	
	public String id() {
		return get(id_prop);
	}

	public String name() {
		return get(name_prop);
	}
	
	public boolean isAbstract() {
		return table()==null;
	}
	
	public String table() {
		return get(table_prop);
	}

	public String column() {
		return get(column_prop);
	}
	
	public void column(String column) {
		if (!id().equals(column))  //copes with a shortcut on meta column
			set(column_prop,column);
	}

	protected Properties properties() {
		return this.properties;
	}
	
	protected String get(String name) {
		
		return properties.contains(name) ? properties.lookup(name).value(String.class) : null;
		
	}
	
	protected void set(String name,Object value, boolean display) {
		
		if (value==null)
				return;
		
		if (properties.contains(name))
			properties.remove(name);

		properties.add(new Property(name,value,false));
		
	}
	
	protected void set(String name,Object value) {
		
		set(name,value,true);
		
	}
	
	

	@Override
	public String toString() {
		return format(this.getClass().getSimpleName()+": id=%s, name=%s, table=%s, col=%s", id(),name(),table(),column());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}
	
	
}
