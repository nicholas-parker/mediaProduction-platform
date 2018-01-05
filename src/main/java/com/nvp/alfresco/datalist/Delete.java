package com.nvp.alfresco.datalist;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.nvp.util.MapperUtil;
import com.nvp.util.RESTResponse;
import com.nvp.util.WebscriptUtil;

public class Delete extends AbstractWebScript {

	private static Log logger = LogFactory.getLog(Write.class);
    
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
     
    	RESTResponse response = new RESTResponse();
    	
    	OutputStream os = res.getOutputStream();
    	try {

    		// get the web script URL parameters
	        Map<String, String> templateArgs = req.getServiceMatch().getTemplateVars();
	        String listName = templateArgs.get("listType");
	        String siteName = templateArgs.get("siteName");
	        String nodeUUID = templateArgs.get("nodeUUID");
	        
	        
            // setup classes to use
	        DataListUtil util = new DataListUtil(this.registry.getSiteService(), this.registry.getNodeService());
	        // MapperUtil mapperUtil = new MapperUtil();
	        // mapperUtil.setNamespaceService(registry.getNamespaceService());
	        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
	        properties.put(ContentModel.PROP_NODE_UUID, nodeUUID);
	       	        
	        util.deleteListItem(siteName, listName, properties);
	        
	        res.setStatus(HTTP_OK);
	        res.setContentEncoding("charset=UTF-8");
	        res.setContentType("application/json");
	        response.setOK();
	        os.write(response.getBytes());
	       
        
    	} catch(Exception e) {

        	logger.error("Error deleting data list item " + e.getMessage());
        	res.setStatus(HTTP_FAIL);
        
        	if(null != e.getMessage()) {
        	  response.setStatusFail(e.getMessage());
        	} else {
        		response.setStatusFail("Error writing new entry");
        	}
        	os.write(response.getBytes()); 
            
            
        }

    	os.close();
    }

}
