package org.virtual.rtms.codelist;

import static org.virtual.rtms.Constants.*;

import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.mutable.codelist.CodeMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.codelist.CodelistMutableBean;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.codelist.CodeMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.codelist.CodelistMutableBeanImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualrepository.Asset;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

public class Table2SdmxCodelist {

	private static Logger log = LoggerFactory.getLogger(Table2SdmxCodelist.class);
	
	public CodelistBean toSdmx(Asset asset, Table table) {
		
		log.info("transforming codelist " + asset.name() + " to sdmx");
		
		 CodelistMutableBean codelist = new CodelistMutableBeanImpl();

		 codelist.setAgencyId("FAO");
	        
	     codelist.setId(asset.name());
	     codelist.addName("en", asset.name());
	     
	     Column codeColumn = table.columns().get(0);

	     //knowledge of current tables for a minimal transform
	     for (Row row : table) {

	    	 CodeMutableBean code = new CodeMutableBeanImpl();
	    
	    	 code.setId(adaptCode(row.get(codeColumn)));
	   	     
	    	 code.addName("en",row.get(RTMS_ATTRIBUTE_NAME_E));
	    	 
	    	 if (row.get(RTMS_ATTRIBUTE_NAME_E)!=null)
	    		 code.addName("fr",row.get(row.get(RTMS_ATTRIBUTE_NAME_E)));
	    	 
	    	 if (row.get(RTMS_ATTRIBUTE_NAME_S)!=null)
	    		 code.addName("es",row.get(row.get(RTMS_ATTRIBUTE_NAME_S)));

	     }
	      
	     
	     return codelist.getImmutableInstance();
		
	}
	
	private String adaptCode(String code) {
		//TODO add to this simple adaptation
        return code.replace(".", "_");
	}
}
