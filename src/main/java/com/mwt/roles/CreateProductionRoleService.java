package com.mwt.roles;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;


/**
 * Creates a Production Role node in the given site.
 * Properties for new role are POSTed to the service.
 * 
 * url parameters
 * @param siteName short name of the site
 * 
 * @author nickp
 *
 */
public class CreateProductionRoleService extends AbstractWebScript {
	
    private static Log logger = LogFactory.getLog(CreateProductionRoleService.class);
    
    private ServiceRegistry registry;
    private ProductionRoleManager roleManager;
    
    private final String URL_PARAM_SITE_NAME = "siteName";
    private final Integer HTTP_OK = 200;
    private final Integer HTTP_FAIL = 500;
    private final String HTTP_OK_CONTENT = "OK";
   
    // for Spring injection 
    public void setServiceRegistry(ServiceRegistry registry) {
     this.registry = registry;
    }

    /**
     * Create a new nvpList:ProductionRole node in the productionRole list for a given site
     */
    public void execute(WebScriptRequest req, WebScriptResponse res) 
  	      throws IOException {
  	     
  	    	OutputStream os = res.getOutputStream();
  	
  	    	roleManager = new ProductionRoleManager();
  	    	roleManager.setServiceRegistry(registry);
  	    	
  	    	try {
  	    		
  	    	    /**
  	    	     * Get the parent node parameter 
  	    	     */
  	    		Map<String, String> templateArgs = req.getServiceMatch().getTemplateVars();
  	    		String siteName = templateArgs.get(URL_PARAM_SITE_NAME);
  	    		
  	    		/**
  	    		 * parse the post content which contains the new role
  	    		 */
  	    		JSONParser parser = new JSONParser();
	            Object jsonO = parser.parse(req.getContent().getReader());
	            JSONObject JsonRoleForm;
	            if (jsonO instanceof JSONObject && jsonO != null)
	            {
	                 JsonRoleForm = (JSONObject)jsonO;
	            }
	            else
	            {
	                 throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Wrong JSON type found " + jsonO);
	            }

	            roleManager.createRole(JsonRoleForm, siteName);
  	    	    
  	            res.setStatus(HTTP_OK);
  	            res.setContentEncoding("charset=UTF-8");
  	            res.setContentType("application/json");
  	            os.write(this.HTTP_OK_CONTENT.getBytes());
  	            os.close();
  	          
  	        } catch(Exception e) {

  	        	logger.error("Error creating new production role " + e.getMessage());

  	        	res.setStatus(HTTP_FAIL);
  	        	if(null != e.getMessage()) {
  	              os.write(e.getMessage().getBytes()); }
  	            os.close();
  	            
  	        }
  	    }

    	
    	

}
