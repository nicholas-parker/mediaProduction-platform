package com.nvp.alfresco.datalist;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.nvp.util.MapperUtil;

public class Read extends AbstractWebScript{

	private static Log logger = LogFactory.getLog(Read.class);
    
    private ServiceRegistry registry;
    
    
    private final Integer HTTP_OK = 200;
    private final Integer HTTP_FAIL = 500;
        
    // for Spring injection 
    public void setServiceRegistry(ServiceRegistry registry) {
     this.registry = registry;
    }

    @SuppressWarnings("unchecked")
	public void execute(WebScriptRequest req, WebScriptResponse res) 
      throws IOException {
     
    	OutputStream os = res.getOutputStream();
    	
    	// the top level JSON object to be returned
    	JSONObject items = new JSONObject();
    	
    	// the 'item' array we will pass back which will contain properties of all the child nodes of dataFolderNode
    	JSONArray itemList = new JSONArray();
    	
    	// a JSON object which will be populated with the properties of each node
        JSONObject jProperties;
        
        // a map which will contain node properties
        Map<QName, Serializable> properties = null;
        
    	try {
    		
	    	/**
	    	 * get the datalist node
	    	 * 		1 - get the root dataList node in the site
	    	 *      2 - get the child node which contains the list, whose name is the required name
	    	 *      3 - iterate through the list node's children which are the items in the list
	    	 */
	        Map<String, String> templateArgs = req.getServiceMatch().getTemplateVars();
	        String listName = templateArgs.get("listType");
	        String siteName = templateArgs.get("siteName");
	        
	        DataListUtil util = new DataListUtil(this.registry.getSiteService(), this.registry.getNodeService());
	        MapperUtil mapperUtil = new MapperUtil();
	        mapperUtil.setNamespaceService(this.registry.getNamespaceService());
	        NodeRef dataFolderNode = util.getDataListFolder(siteName, listName);
	        
	        if(null != dataFolderNode) {
	        	
		        // run through the child nodes and put properties into the 'item' list
	        	List<ChildAssociationRef> childNodeList = this.registry.getNodeService().getChildAssocs(dataFolderNode);
	        	for(ChildAssociationRef childNodeRef : childNodeList){
		            properties = this.registry.getNodeService().getProperties(childNodeRef.getChildRef());
		            
		            // convert the QName properties into <namespace>_<filedname> format for use in Javascript
		            Map<String, String> flatNameMap = mapperUtil.QNameListToFlatMap(properties);
		            jProperties = new JSONObject(flatNameMap);
	                itemList.add(jProperties);
		        }
	
	        }
	              
	        items.put("items", itemList);
			
	        res.setStatus(HTTP_OK);
	        res.setContentEncoding("charset=UTF-8");
	        res.setContentType("application/json");
	        if(null != items) {
	          os.write(items.toJSONString().getBytes());
	          os.close();
	        }
        
    	} catch(Exception e) {

        	logger.error("Error reading data list " + e.getMessage());
        	res.setStatus(HTTP_FAIL);
        	if(null != e.getMessage()) {
              os.write(e.getMessage().getBytes()); }
            os.close();
            
        }
    }
    
}
