package com.mwt.activiti.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;

import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.nvp.alfresco.datalist.Create;
import com.nvp.util.MapperUtil;
import com.nvp.util.RESTResponse;
import com.nvp.util.WebscriptUtil;

/**
 * Starts a workflow instance. JSON Post body is as folows
 * 
 * {
 *    processName : "name of process",
 *    properties : [ array of properties which are applied to the workflow ]
 * }
 * 
 * JSON 200 response is
 * 
 * {
 *    message : " <Name> process started ",
 *    processId : 1234
 * }
 * 
 * @author Nick
 *
 */
public class StartWorkflowService extends AbstractWebScript {
	
	private static Log logger = LogFactory.getLog(Create.class);
	
    private ServiceRegistry registry;
    
	private final Integer HTTP_OK = 200;
    private final Integer HTTP_FAIL = 500;
        
    private final String JSON_POST_PROCESSNAME = "processName";
    private final String JSON_POST_PROPERTIES = "properties";
    private final String JSON_RESP_PROCESSID = "processId";
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
    
    		/**
    		 * check we have all we need to start a process
    		 */
    		JSONObject jsonPost = WebscriptUtil.getJsonPost(req);
            if(jsonPost.containsKey(JSON_POST_PROCESSNAME) == Boolean.FALSE) {
            	throw new Exception("Request did not contain 'processName' attribute");
            }
            
            String processName = jsonPost.get(JSON_POST_PROCESSNAME).toString();
            if(processName.isEmpty() == Boolean.TRUE) {
            	throw new Exception("Process name value not set in request, is empty");
            }
            
            /**
             * get the properties to set in the workflow
   2          */
            Map<QName, Serializable> workflowProps = new HashMap<QName, Serializable>();
            if(jsonPost.containsKey(JSON_POST_PROPERTIES) && jsonPost.get(JSON_POST_PROPERTIES).toString().isEmpty() == Boolean.FALSE) {
            	
            	JSONObject jsonProperties = (JSONObject) jsonPost.get(JSON_POST_PROPERTIES);
            	MapperUtil mapperUtil = new MapperUtil();
            	mapperUtil.setNamespaceService(this.registry.getNamespaceService());
            	workflowProps = mapperUtil.JSONToMap(jsonProperties);
            }
        
            
            /**
             * start the workflow
             */
            WorkflowService workflowService = this.registry.getWorkflowService();
            WorkflowDefinition workflowDefinition = workflowService.getDefinitionByName(processName);
            NodeRef packageNodeRef = workflowService.createPackage(null);
            workflowProps.put(WorkflowModel.TYPE_PACKAGE , packageNodeRef);
        
            WorkflowPath wfPath = workflowService.startWorkflow(workflowDefinition.getId(), workflowProps);
	  	    String wfId = wfPath.getId();
	  	    
	  	    response.setOK();
	  	    response.setEntry(JSON_RESP_PROCESSID, wfId);
	  	    res.setStatus(HTTP_OK);
	  	    os.write(response.getBytes()); 
            os.close();
  		
    	} catch (Exception e) {

    		e.printStackTrace();
    		logger.error("Error starting workflow " + e.getMessage());
        	res.setStatus(HTTP_FAIL);
        
        	if(null != e.getMessage()) {
        	  response.setStatusFail(e.getMessage());
        	} else {
        		response.setStatusFail("Error starting workflow");
        	}
        	os.write(response.getBytes()); 
            os.close();
    		
    	}
    }

}
