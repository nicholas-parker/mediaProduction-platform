package com.mwt.contract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import com.mwt.contract.ContractServiceException;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.production.ContractDocumentTypes;
import com.mwt.production.ProductionDocumentModel;
import com.nvp.util.DocUtil;
   
public class ContractService {

	private ServiceRegistry registry;
	
	public static String CONTRACT_COMPONENT_ID = "contractContainer";
	public static String CONTRACT_CONTAINER_NAME = "Contracts";
    
    public void setServiceRegistry(ServiceRegistry registry) {
        this.registry = registry;
       }
    
 
    public String createContractDocumentFullFilename(String contractName) throws ContractServiceException {
    	
    	if(null == contractName || contractName.isEmpty()) {
    		throw new ContractServiceException("Unable to create contract, contract name is null", null);
    	}
    	StringBuilder contractDocumentName = new StringBuilder();
		contractDocumentName.append(contractName );
		contractDocumentName.append(" ");
		contractDocumentName.append("contract.docx");
        return contractDocumentName.toString();
    }
    
    /**
     * 
     * Creates a new contract from a template and applies the properties 
     * provided to the contract node but does not place them into the document content
     * 
     * @param siteName
     * @param contractName
     * @param properties
     * @param templateNode
     * @return
     * @throws ContractServiceException
     */
    public NodeRef createContractForProduction(String siteName, String contractName, Map<QName, Serializable> properties, NodeRef templateNode ) throws ContractServiceException {
    
    	/**
    	 * check the contract name, should have a .docx extension or similar
    	 * 
    	 */
    	if(null == contractName || contractName.isEmpty()) {
    		throw new ContractServiceException("Unable to create contract, contract name is null", null);
    	}
    
		/**
		 * 
		 * find the container to store the contract document in
		 * 
		 */
		if(null == siteName || siteName.isEmpty()) {
    		throw new ContractServiceException("Unable to create contract, site name is null", null);
    	}
		NodeRef contractContainer = getSiteContractContainer(siteName);
		
		/**
		 * 
		 * create a new node with the content of the template
		 * 
		 */
		NodeRef contractDocumentNode = null;
		try {
		
			contractDocumentNode = DocUtil.createDocumentFromOriginal(contractName, 
					                                                  templateNode, 
					                                                  contractContainer, 
					                                                  this.registry);

		} catch (Exception e) { 
			
			ContractServiceException cse = new ContractServiceException("Unable to create the contract document " + e.getMessage(), e);
			cse.printStackTrace();
			throw cse;
			
		}
		
		
		/**
		 * 
		 * set the 'document' aspect & properties in the node
		 * 
		 */
		 Map<QName, Serializable> documentAspectProperties = new HashMap<QName, Serializable>();
		 documentAspectProperties.put(ProductionDocumentModel.QN_DOC_TYPE, ContractDocumentTypes.EmploymentContract);
		 documentAspectProperties.put(ProductionDocumentModel.QN_DOC_CATEGORY, ContractDocumentTypes.Category);

		 if( this.registry.getNodeService().hasAspect(contractDocumentNode, ProductionDocumentModel.QN_PROD_DOCUMENT_ASPECT)) {
			 this.registry.getNodeService().setProperties(contractDocumentNode, documentAspectProperties);
		 } else {
			 this.registry.getNodeService().addAspect(contractDocumentNode, ProductionDocumentModel.QN_PROD_DOCUMENT_ASPECT, documentAspectProperties);
		 }

		 return contractDocumentNode;
		 
    }
    
/*    public NodeRef createServiceContractForRole(Map<QName, Serializable> contractProps, Map<QName, Serializable> serviceProps, String contractFileName, NodeRef contractTemplate ) throws ContractServiceException {
    	

    	NodeRef contractDocument = null;
    	
    	if(contractTemplate != null) {
    	
    		try {
    			
    		
	    		*//**
	    		 * If we are here then there is a template document which should
	    		 * be used for the contract. Copy the contract template to the
	    		 * contract document 
	    		 *//*
	    		contractDocument = createFromTemplate(contractTemplate, contractFileName, null);
	    		
	    		*//**
	    		 * set the contract and service aspects
	    		 *//*
	    		this.setContractDocumentAspect(contractDocument, contractProps);
	
	    		*//**
	    		 * set the service aspect
	    		 *//*
	    		// this.setServicePeriodAspect(contractDocument, serviceProps);
	    		
	    		*//**
				 * Update the new word document with data from the properties
				 *//*
				WordTransformer transformer = new WordTransformer();
				transformer.updateWordDocument(contractDocument, contractProps, registry);
				
            } catch (Exception e) {
    		
            	e.printStackTrace();
            	throw new ContractServiceException();
            	
    		}
    		
    	} else {
    		
    		*//**
    		 * create an empty node which content must be added to
    		 *//*
    		contractDocument = createEmptyDocumentNode(contractFileName);
    		
    		
    	}
    	
    	return contractDocument;
    }
*/    
    
    /**
     * Create a new contract document by copying from a template document
     * 
     * @param template
     * @param name
     * @param properties
     * @return
     * @throws ContractServiceException 
     */
    
   /*  DEPRECATED
	private NodeRef createFromTemplate(NodeRef template, String name, Map<QName, Serializable> properties) throws ContractServiceException {
		
		try {
			
			*//**
			 * get the contracts container for this site, we will copy the
			 * template into this folder.
			 *//*
			SiteService siteService = registry.getSiteService();
			SiteInfo siteRef = siteService.getSite(template);
			NodeRef contractsContainer = getSiteContractContainer(siteRef.getShortName());
			
			*//**
			 * copy the template into this folder,
			 * apply the contract aspect,
			 *//*
			FileFolderService folderService = this.registry.getFileFolderService();
			FileInfo contract = folderService.copy(template, contractsContainer, name);

			
			
			return contract.getNodeRef();
			
		
		} catch(Exception e) {
			
			throw new ContractServiceException("Unable to create contract template.", e);
			
		}  
	}
*/	

		
   /**
    * 
    * Obtain the NodeRef for the Contracts container for a specific site.
    * If the container does not exist it is created in the DocumentLibrary
    * 
    * @param siteShortName shortName of the site we want the contracts container for
    * @return NodeRef of the Contracts container
    * @throws ContractServiceException 
    */
   private NodeRef getSiteContractContainer(String siteShortName) throws ContractServiceException {
	    
	    SiteService siteService = registry.getSiteService();
		FileFolderService fileFolderService = registry.getFileFolderService();
		NodeService nodeService = registry.getNodeService();
		SearchService searchService = registry.getSearchService();
		
		NodeRef contractsContainer = null;
		NodeRef docLibContainer = siteService.getContainer(siteShortName, SiteService.DOCUMENT_LIBRARY);
		if(docLibContainer == null) {
			throw new ContractServiceException("No document library in this site " + siteShortName, null); }

		try {


			List<String> paths = new ArrayList<String>(1);
	        paths.add(ContractDocumentModel.CONTRACT_CONTAINER_NAME);
			FileInfo fileInfo = fileFolderService.resolveNamePath(docLibContainer, paths);
			if (!fileInfo.isFolder())
	        {
	            throw new ContractServiceException("ERROR: looking for Contracts container in site " + siteShortName + ", but the item is not a foolder", null);
	        }
			contractsContainer = fileInfo.getNodeRef();
		  
		} catch( FileNotFoundException fnfe) {
			
			/*
			 * 
			 * we are here because we didn't find a contracts container so create one
			 * 
			 */
			contractsContainer = fileFolderService.create(docLibContainer, 
					                                      CONTRACT_CONTAINER_NAME, 
					                                      ContractDocumentModel.QN_CONTRACT_CONTAINER_TYPE).getNodeRef();
			
			Map<QName, Serializable> containerProperties = new HashMap<QName, Serializable>();
			containerProperties.put(SiteModel.PROP_COMPONENT_ID, ContractDocumentModel.CONTRACT_CONTAINER_ID);
			nodeService.addAspect(contractsContainer , SiteModel.ASPECT_SITE_CONTAINER, containerProperties);
		
			
		} catch( Exception e) {
	
			ContractServiceException cse = new ContractServiceException("ERROR: very unexpected thing happened whilst searching for contracts container, " + e.getMessage(), null);
			cse.printStackTrace();
			throw cse;
			
		}

		return contractsContainer;
   }

   /**
    * Create the contracts folder under the Document Library for the site
    * 
    * @param siteShortName
    * @return
    * @throws ContractServiceException
    */
   public NodeRef createContractContainer(String siteShortName) throws ContractServiceException {

	   SiteService siteService = registry.getSiteService();
	   FileFolderService fileFolderService = registry.getFileFolderService();
	   NodeService nodeService = registry.getNodeService();

	   NodeRef contractsContainer = null;
	   try {
		   
		   NodeRef docLibContainer = siteService.getContainer(siteShortName, SiteService.DOCUMENT_LIBRARY);
		   
		   contractsContainer = fileFolderService.create(docLibContainer, 
                   CONTRACT_CONTAINER_NAME, 
                   ContractDocumentModel.QN_CONTRACT_CONTAINER_TYPE).getNodeRef();

		   Map<QName, Serializable> containerProperties = new HashMap<QName, Serializable>();
		   containerProperties.put(SiteModel.PROP_COMPONENT_ID, ContractDocumentModel.CONTRACT_CONTAINER_ID);
		   nodeService.addAspect(contractsContainer , SiteModel.ASPECT_SITE_CONTAINER, containerProperties);

	   } catch (Exception e) {

			ContractServiceException cse = new ContractServiceException("ERROR: very unexpected thing happened whilst creating contracts container for site [" + siteShortName + "], " + e.getMessage(), null);
			cse.printStackTrace();
			throw cse;

	   }
	   return contractsContainer;
   }
      
   /**
    * sets the properties of the ContractDocument aspect to a node,
    * if the node does not have the aspect it is added
    * 
    * @param docNode a nodeRef which is a contract document
    * @param props the map of properties which are set to the aspect.
    */
   private void setContractDocumentAspect(NodeRef docNode, Map<QName, Serializable> props){
	    
	  NodeService nodeService = registry.getNodeService();
	  if(!nodeService.hasAspect(docNode, ContractDocumentModel.QN_CONTRACT_ASPECT)){
		  
		  nodeService.addAspect(docNode, ContractDocumentModel.QN_CONTRACT_ASPECT, props);  
	  
	  } else {
		
		  nodeService.setProperties(docNode, props);
	  }
	   
   }
   
   /**
    * copies the service period properties into the ServicePeriod aspect.
    * If aspect does not exist creates the aspect.
    * 
    * @param docNode
    * @param props
    */
   private void setServicePeriodAspect(NodeRef docNode, Map<QName, Serializable> props) {

		  NodeService nodeService = registry.getNodeService();
		  if(!nodeService.hasAspect(docNode, ContractDocumentModel.QN_CONTRACT_ASPECT)){
			  
			  nodeService.addAspect(docNode, ContractDocumentModel.QN_CONTRACT_ASPECT, props);  
		  
		  } else {
			
			  nodeService.setProperties(docNode, props);
		  }

   }
   
   /**
    * 
    * given a contract node uuid it calculates the total value of the contract
    * @param uuid
    * @return
    */
   public float calculateTotalContractValue(String uuid) {
	   
	   try {
	   
		   NodeRef contractNodeRef = new NodeRef("workspace", "SpacesStore", uuid);
		   NodeService nodeService = this.registry.getNodeService();
		   Map<QName, Serializable> contractProperties = nodeService.getProperties(contractNodeRef);
	   
	   } catch (Exception e) {
		   
	   }
	   
	   return (Float) null;
	   
   }
}
