package com.mwt.contract;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.nvp.util.NodeRefUtil;
import com.nvp.util.RESTResponse;

public class MergeContractPropertiesToContentService extends AbstractWebScript {

			private static Log logger = LogFactory.getLog(MergeContractPropertiesToContentService.class);
		    
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
			        
			        String nodeId = templateArgs.get("nodeId");
                    NodeRef contractNode = NodeRefUtil.NodeReffromUUID(nodeId);
			        
                    String saveAsPdf = templateArgs.get("saveAsPdf");
			        boolean _saveAsPdf;
			        if(null != saveAsPdf && saveAsPdf.contentEquals("true")) {
			          _saveAsPdf = true;
		    	    } else {
		    	      _saveAsPdf = false;
		    	    }
		    	    
                    // the top level JSON object to be returned
			    	JSONObject result = new JSONObject();
			        
			    	// merge the node properties into the content
			        ContractService contractService = new ContractService();
			        contractService.setServiceRegistry(registry);
			    	contractService.mergeContractPropertiesToContent(contractNode, _saveAsPdf);
			        
			    	// create the response
			        res.setStatus(HTTP_OK);
			        res.setContentEncoding("charset=UTF-8");
			        res.setContentType("application/json");
			        response.setOK();
			        os.write(result.toJSONString().getBytes());
		        
		    	} catch(Exception e) {

		        	logger.error("Error merging node properties into content " + e.getMessage());
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
