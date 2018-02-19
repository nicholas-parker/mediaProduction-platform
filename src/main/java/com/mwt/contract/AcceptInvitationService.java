package com.mwt.contract;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.invitation.InvitationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.nvp.alfresco.datalist.Create;
import com.nvp.util.MapperUtil;
import com.nvp.util.RESTResponse;

public class AcceptInvitationService extends AbstractWebScript {
	
	private static Log logger = LogFactory.getLog(Create.class);
    
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
	        String invitationId = templateArgs.get("invitationId");
	        if(null == invitationId || invitationId.isEmpty()) {
	        	throw new Exception("Unable to accept invitation, invitationId was null");
	        }
	        String ticket = templateArgs.get("ticket");
	        if(null == invitationId || invitationId.isEmpty()) {
	        	throw new Exception("Unable to accept invitation, ticket was null");
	        }

	        
	        MapperUtil mapperUtil = new MapperUtil();
	        mapperUtil.setNamespaceService(registry.getNamespaceService());
	        
	        // accept
	        InvitationService invitationService = registry.getInvitationService();
	        invitationService.accept(invitationId, ticket);
	        
	        res.setStatus(HTTP_OK);
	        res.setContentEncoding("charset=UTF-8");
	        res.setContentType("application/json");
	        response.setOK();
	        os.write(response.getBytes());

    	} catch (Exception e) {

    		logger.error("Error accepting invitation " + e.getMessage());
        	res.setStatus(HTTP_FAIL);
        
        	if(null != e.getMessage()) {
        	  response.setStatusFail(e.getMessage());
        	} else {
        		response.setStatusFail("Error accepting invitation");
        	}
        	os.write(response.getBytes()); 
            os.close();
    		
    	}

    }
}
