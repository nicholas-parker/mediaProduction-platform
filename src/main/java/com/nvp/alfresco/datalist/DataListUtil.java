package com.nvp.alfresco.datalist;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import com.mwt.roles.DefaultRoleModel;
import com.mwt.roles.ProductionRoleModel;

public class DataListUtil {

	private NodeService nodeService;
	private SiteService siteService;
	private final String SITE_DATA_LIST_CONTAINER = "dataLists";
	
	public DataListUtil(SiteService siteService, NodeService nodeService) {
		
		this.nodeService = nodeService;
		this.siteService = siteService;
		 
	}
	
	/**
	 * Returns the root folder for data lists in a site, under this folder is a folder for each list
	 * 
	 * @param siteName
	 * @return
	 */
	public NodeRef getRootDataFolder(String siteName) {
		
		NodeRef siteNode = this.siteService.getContainer(siteName, SITE_DATA_LIST_CONTAINER);
		if (null == siteNode) {
		    siteNode = this.siteService.createContainer(siteName, SITE_DATA_LIST_CONTAINER, null, null); }
		return siteNode;
		    
	}
	
	/**
	 * Get the folder which contains the list
	 * 
	 * @param siteName
	 * @param listName
	 * @return
	 */
	public NodeRef getDataListFolder(String siteName, String listName) {
		
			
		NodeRef siteRootDataNode = getRootDataFolder(siteName);

			List<ChildAssociationRef> childs = this.nodeService.getChildAssocs(siteRootDataNode);
			for (ChildAssociationRef childRef : childs) {
				NodeRef nodeChildRef = childRef.getChildRef();
				String name = (String) nodeService.getProperty(nodeChildRef, ContentModel.PROP_NAME);
				if (null != name && name.contentEquals(listName)) {
					return nodeChildRef;
				}
			}
		

		// no folder found
		return null;
	}
	
	/**
	 * Create a new entry in the data list
	 * 
	 * @param siteName
	 * @param listName
	 * @param roleProperties
	 * @return
	 */
	public Map<QName, Serializable> createNode(String siteName, String listName, Map<QName, Serializable> properties) {
		
		NodeRef newNode = null;
		NodeRef folderNode = getDataListFolder(siteName, listName);

		if( null == folderNode ) {
			
			// TODO - throw exception
			
		}
		
		// TODO - if we go this way get the model type from properties in the parent node, for the moment do something else
		// String modelNamespace = (String) nodeService.getProperty(folderNode, ListModel.PROP_METAMODEL_URI);
		// String modelType = (String) nodeService.getProperty(folderNode, ListModel.PROP_METAMODEL_TYPE);
		// QName dataType = QName.createQName(modelNamespace, modelType);
		
		// TODO - remove this line whe we work out how to set the type of the data model we are writing as per the above block
		QName dataType = DefaultRoleModel.QN_DEFAULT_ROLE; 
		
		String nodeName = java.util.UUID.randomUUID().toString();
		 
		try {
			
			newNode = nodeService.createNode(
	                  folderNode, 
	                  ContentModel.ASSOC_CONTAINS, 
	                  QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
	                      QName.createValidLocalName(listName)),
	                  dataType, 
	                  properties).getChildRef();

			properties = nodeService.getProperties(newNode);
			
		} catch (Exception e) {
			
			// TODO - add logging and error handling
			throw e;
			
		}
		
		return properties;
	}
	
	/**
	 * Create a folder to contain the data list
	 * 
	 * @param siteName
	 * @param listName
	 * @return
	 */
	public Map<QName, Serializable> createDataFolder(String siteName, String listName, String modelName) {
		 
	    NodeRef rootNode = getRootDataFolder(siteName);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
		
		try {
		
		
			properties.put(ContentModel.PROP_NAME, listName);
			
			NodeRef dataListNode = nodeService.createNode(
	                  rootNode, 
	                  ContentModel.ASSOC_CONTAINS, 
	                  QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
	                      QName.createValidLocalName(listName)),
	                  ContentModel.TYPE_FOLDER, 
	                  properties).getChildRef();
			
			properties = nodeService.getProperties(dataListNode);

		} catch (Exception e) {
			
			// TODO - add logging and error handling
			throw e;
			
		}
		
		return properties;

	}
	
	/**
	 * Update a nodes properties which is in the data list container
	 * 
	 * @param siteName
	 * @param listName
	 * @param roleProperties
	 * @return NodeRef to the updated node
	 */
	public Map<QName, Serializable> updateListItem(String siteName, String listName, Map<QName, Serializable> properties) throws Exception {
		
		if(properties.containsKey(ContentModel.PROP_NODE_UUID) == Boolean.FALSE) {
			throw new Exception("Unable to update data list item, PROP_NODE_UUID not provided in node properties"); }
		
		if(properties.get(ContentModel.PROP_NODE_UUID).toString().isEmpty()) {
			throw new Exception("Unable to update data list item, PROP_NODE_UUID is empty"); }
		
		NodeRef node = new NodeRef("workspace","SpacesStore", properties.get(ContentModel.PROP_NODE_UUID).toString());

		/** this can be a partial update */
		nodeService.setProperties(node,properties);
		
		/** get all the properties and return them */
		properties = nodeService.getProperties(node);
		return properties;
	}
	
	/**
	 * Deletes a list node, does not check if user is in site, relies on Alfresco ACL
	 * 
	 * @param siteName
	 * @param listName
	 * @param properties. Must contain PROP_NODE_UUID which is used to identify node
	 * @throws Exception
	 */
	public void deleteListItem(String siteName, String listName, Map<QName, Serializable> properties) throws Exception {
		
		if(properties.containsKey(ContentModel.PROP_NODE_UUID) == Boolean.FALSE) {
			throw new Exception("Unable to delete data list item, PROP_NODE_UUID not provided in node properties"); }
		
		if(properties.get(ContentModel.PROP_NODE_UUID).toString().isEmpty()) {
			throw new Exception("Unable to delete data list item, PROP_NODE_UUID is empty"); }
		
		NodeRef node = new NodeRef("workspace","SpacesStore", properties.get(ContentModel.PROP_NODE_UUID).toString());
		nodeService.deleteNode(node);
		
	}
}
