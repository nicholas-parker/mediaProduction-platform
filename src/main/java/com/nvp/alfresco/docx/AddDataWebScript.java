package com.nvp.alfresco.docx;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;


public class AddDataWebScript extends AbstractWebScript {
	
    private static Log logger = LogFactory.getLog(AddDataWebScript.class);
    
    private ServiceRegistry registry;
    
    private final String JSON_NODEREF = "nodeRef";
    private final String JSON_DATA = "data";
    
    private final Integer HTTP_OK = 200;
    private final Integer HTTP_FAIL = 500;
    private final String HTTP_OK_CONTENT = "OK";
    
    // for Spring injection 
    public void setServiceRegistry(ServiceRegistry registry) {
     this.registry = registry;
    }

    public void execute(WebScriptRequest req, WebScriptResponse res) 
      throws IOException {
     
    	// NOTE: This web script must be executed in a HTTP Servlet environment
        /**
    	if (!(req instanceof WebScriptServletRequest)) {
         throw new WebScriptException(
           "Content retrieval must be executed in HTTP Servlet environment");
        }
    	*/
    	
        Content c = req.getContent();
        if (c == null)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Missing POST body.");
        }
        
        // Process the JSON       
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        
        try {
        	
            Object jsonO = null;
            jsonO = parser.parse(req.getContent().getReader());
             
            if (jsonO instanceof JSONObject && jsonO != null)
            {
                 json = (JSONObject)jsonO;
            }
            else
            {
                 throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Wrong JSON type found " + jsonO);
            }
          
          NodeRef nodeRef = new NodeRef((String) json.get(JSON_NODEREF));
          String data = (String) json.get(JSON_DATA);
    
          WordRenderer wr = new WordRenderer();
          wr.setServiceRegistry(registry);
          wr.renderWordDocNode(nodeRef, data);

          res.setStatus(HTTP_OK);
          OutputStream os = res.getOutputStream();
          os.write(HTTP_OK_CONTENT.getBytes());
          os.close();
          
        } catch(Exception e) {

        	logger.error("Error rendering document " + e.getMessage());

        	res.setStatus(HTTP_FAIL);
        	OutputStream os = res.getOutputStream();
            os.write(e.getMessage().getBytes());
            os.close();
            
        }
    }
    
    

}
