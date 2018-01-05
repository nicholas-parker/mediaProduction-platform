package com.nvp.alfresco.datalist;


	import java.io.IOException;
	import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.extensions.webscripts.AbstractWebScript;
	import org.springframework.extensions.webscripts.WebScriptRequest;
	import org.springframework.extensions.webscripts.WebScriptResponse;
	


	public class GetDatalistItems extends AbstractWebScript {
		
	    private static Log logger = LogFactory.getLog(GetDatalistItems.class);
	    
	    private ServiceRegistry registry;
	    
	    
	    private final Integer HTTP_OK = 200;
	    private final Integer HTTP_FAIL = 500;
	    private final String HTTP_OK_CONTENT = "OK";
	    
	    // for Spring injection 
	    public void setServiceRegistry(ServiceRegistry registry) {
	     this.registry = registry;
	    }

	    public void execute(WebScriptRequest req, WebScriptResponse res) 
	      throws IOException {
	     
	    	OutputStream os = res.getOutputStream();
	    	
	    	/**
	    	 * The JSONObject containing the roles which is returned
	    	 * to the caller
	    	 */
	    	JSONObject items = null;
	    	
	    	try {
	    		
	    	/**
	    	 * get the datalist node
	    	 * 		1 - get the root dataList node in the site
	    	 *      2 - get the child node which contains the list, whose name is the required name
	    	 *      3 - iterate through the list node's children which are the items in the list
	    	 */
	        Map<String, String> templateArgs = req.getServiceMatch().getTemplateVars();
	        String listName = templateArgs.get("listType");
	        SiteService siteService = registry.getSiteService();
	        NodeRef dataListContainer = siteService.getContainer(templateArgs.get("siteName"), "dataLists");
            NodeService nodes = registry.getNodeService();
            
            List<ChildAssociationRef> children = nodes.getChildAssocs(dataListContainer);
            for(ChildAssociationRef child : children){
            
            	Map<QName, Serializable> nodeListProps = nodes.getProperties(child.getChildRef());
                if(nodeListProps.containsKey(ContentModel.PROP_TITLE)) {
                	String cm_title = (String) (nodeListProps.get(ContentModel.PROP_TITLE));
                	if( cm_title.contentEquals(listName) ) {
                		List<ChildAssociationRef> productionRoles = nodes.getChildAssocs(child.getChildRef());
                		
                		/**
                		 * Found the Production Roles node.
                		 * Run through the children as these are the actual roles
                		 */
                		
                		JSONArray itemList = new JSONArray();
            	        JSONObject jProperties;
            	        Map properties = null;
            	        for(ChildAssociationRef productionRole : productionRoles){
            	            properties = nodes.getProperties(productionRole.getChildRef());
            	            Map<String, Object> propertiesNoNamespace = removeNamespace(properties);
                            jProperties = new JSONObject(propertiesNoNamespace);
                            itemList.add(jProperties);
            	        }

            	        items = new JSONObject();
            	        items.put("items", itemList);
    		
                		
                	}
                }
            
            
            
            }
            
            	        
	        /*****************************************************
	        	
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
	          
	          
	          String data = (String) json.get(JSON_DATA);
	    
	          WordRenderer wr = new WordRenderer();
	          wr.setServiceRegistry(registry);
	          wr.renderWordDocNode(nodeRef, data);
            */
	        
	          res.setStatus(HTTP_OK);
	          res.setContentEncoding("charset=UTF-8");
	          res.setContentType("application/json");
	          if(null != items) {
	            os.write(items.toJSONString().getBytes());
	            os.close();
	          }
	        } catch(Exception e) {

	        	logger.error("Error rendering document " + e.getMessage());

	        	res.setStatus(HTTP_FAIL);
	        	if(null != e.getMessage()) {
	              os.write(e.getMessage().getBytes()); }
	            os.close();
	            
	        }
	    }

	    /**
	     * 
	     * This function takes a map where the key is a QName and replaces
	     * the key with a new key made up of the QName URI prefix '_' QName localName.
	     * This is so the name works in the standard Alfresco format.
	     * 
	     * @param source
	     * @return
	     */
	    private Map<String, Object> removeNamespace(Map<QName, Object> source) throws Exception {
	    	
	    	HashMap<String, String> nsPrefixes = new HashMap<String, String>();
	    	HashMap<String, Object> response =  new HashMap<String, Object>();
	    	StringBuilder concatName = new StringBuilder();
	    	NamespaceService nsService = registry.getNamespaceService();
	    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    		    	
	    	for(Map.Entry<QName, Object> entry : source.entrySet()){
	    		
	    		String key;
	    		String value = null;
	    		/**
	    		 * add the namespace prefix to the front of the local name
	    		 * cm:content becomes cm_content.
	    		 */
	    		if(!nsPrefixes.containsKey(entry.getKey().getNamespaceURI())){
	    			Collection<String> prefixes = nsService.getPrefixes(entry.getKey().getNamespaceURI());
	    			if(!prefixes.isEmpty()){
	    				String[] nsP = new String[2];
	    				prefixes.toArray(nsP);
	    				nsPrefixes.put(entry.getKey().getNamespaceURI(), nsP[0]);
	    			}
	    		}
	    		concatName.delete(0, concatName.length());
	    		concatName.append(nsPrefixes.get(entry.getKey().getNamespaceURI()));
	    		concatName.append("_");
	    		concatName.append(entry.getKey().getLocalName());
	    		key = concatName.toString();
	    		
	    		/**
	    		 * Whilst we are at it change the data format to DD/MM/YYYY
	    		 */
	    		if(null != entry.getValue() && entry.getValue().getClass() == Date.class){
	    			
	    			try {
	    			
	    				Date d = (Date) entry.getValue();
		    		    if( null != d) {
		    		      value = dateFormat.format(d); }
		    		    
	    			} catch (Exception e) {
	    				
	    				throw new Exception("Error formatting item " + entry.getKey().getLocalName(), e);
	    			}
	    		    
	    		} else {
	    			
	    			if(entry.getValue() == null) {
	    		        value = null;
	    			} else {
	    				value = entry.getValue().toString();
	    			}
	    		}
	    		
	    		/**
	    		 * ensure that all values are in quotes, makes it simpler rather than allowing numerics with no quotes
	    		 */
                if(!entry.getKey().toString().equals("sys_locale")){
                	response.put(key, value);
                }
	    	}
	    	return response;
	    	
	    }
}
