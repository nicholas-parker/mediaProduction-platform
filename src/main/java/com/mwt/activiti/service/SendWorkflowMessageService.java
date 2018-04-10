package com.mwt.activiti.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.alfresco.service.ServiceRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.json.simple.parser.ParseException;

import com.nvp.util.RESTResponse;
import com.nvp.util.WebscriptUtil;

public class SendWorkflowMessageService extends AbstractWebScript {

	private static Log logger = LogFactory.getLog(SendWorkflowMessageService.class);
	
	private RuntimeService runtimeService;
	
    private ServiceRegistry registry;
    
	private final Integer HTTP_OK = 200;
    private final Integer HTTP_FAIL = 500;
        
    
    /**
     * 
     *  Spring provides the registry instance view XML wiring
     * @param registry
     * 
     */
    public void setServiceRegistry(ServiceRegistry registry) {
    	
    
        this.registry = registry;
    
    }

	/**
	 * 
	 * Spring provides the instance of the Activiti runtime service via the XML wiring
	 * 
	 * @param runtimeService
	 */
	public void setRuntimeService(RuntimeService runtimeService) {
		
		if(null == runtimeService) {
			
			logger.error("the provided runtimeservice instance is null");
			
		} else {
		
			this.runtimeService = runtimeService;
		}
		
	}

	/**
	 * 
	 * execute the web script request
	 * 
	 */
    @SuppressWarnings("unchecked")
	public void execute(WebScriptRequest req, WebScriptResponse res) 
      throws IOException {
     
    	RESTResponse response = new RESTResponse();
    	String processId = "";
    	String messageName = "";
    	
    	OutputStream os = res.getOutputStream();
    	try {
    
    		/**
    		 * check we have all we need to start a process
    		 */
    		Map<String, String> templateArgs = req.getServiceMatch().getTemplateVars();
	        processId = templateArgs.get("processId");
	        messageName = templateArgs.get("messageName");
	        logger.info("Sending message [" + messageName + "] to process [" + processId);

    		// JSONObject jsonPost = WebscriptUtil.getJsonPost(req);
    
	        Execution exec = runtimeService.createExecutionQuery().processInstanceId(processId).messageEventSubscriptionName(messageName).singleResult();
	        if(null == exec) {
	        	logger.error("No one subscribed to this message");
	        } else {
	        	logger.error("This is the subscribed execution id " + exec.getId().toString());	
	        	this.runtimeService.messageEventReceived(messageName, exec.getId());
	        }
    		
     	    response.setOK();
	  	    res.setStatus(HTTP_OK);
	  	    os.write(response.getBytes()); 
            os.close();
  	
/*    	} catch (ParseException pe) {

    		String errMsg = "Error sending message to process. Unable to parse the payload into a JSON object";
    		logger.error(errMsg);
    		pe.printStackTrace();
        	res.setStatus(HTTP_FAIL);
        
        	if(null != pe.getMessage()) {
        	  response.setStatusFail(errMsg + pe.getMessage());
        	} else {
        		response.setStatusFail(errMsg);    	} catch (ParseException pe) {

    		String errMsg = "Error sending message to process. Unable to parse the payload into a JSON object";
    		logger.error(errMsg);
    		pe.printStackTrace();
        	res.setStatus(HTTP_FAIL);
        
        	if(null != pe.getMessage()) {
        	  response.setStatusFail(errMsg + pe.getMessage());
        	} else {
        		response.setStatusFail(errMsg);
        	}
        	os.write(response.getBytes()); 
            os.close();

        	}
        	os.write(response.getBytes()); 
            os.close();
*/
    	} catch (ActivitiObjectNotFoundException onf) {
    		
    		String errMsg = "Error sending message to process. Process Id [" + processId + "] cannot be found";
    		logger.error(errMsg);
    		onf.printStackTrace();
        	res.setStatus(HTTP_FAIL);
        
        	if(null != onf.getMessage()) {
        	  response.setStatusFail(errMsg + onf.getMessage());
        	} else {
        		response.setStatusFail(errMsg);
        	}
        	os.write(response.getBytes()); 
            os.close();

            
    	} catch (ActivitiException ae) {

    		String errMsg = "Error sending message to process. Process Id [" + processId + "] has not subscribed to message [" + messageName + "]";
    		logger.error(errMsg);
    		ae.printStackTrace();
        	res.setStatus(HTTP_FAIL);
        
        	if(null != ae.getMessage()) {
        	  response.setStatusFail(errMsg + ae.getMessage());
        	} else {
        		response.setStatusFail(errMsg);
        	}
        	os.write(response.getBytes()); 
            os.close();

    	}
    	
    }
}
