package com.mwt.contract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import com.mwt.contract.ContractServiceException;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.contract.model.RegisteredOrgModel;
import com.mwt.contract.model.ServicePeriodModel;
import com.mwt.contract.propertyBuilder.ContractContentBuilder;
import com.mwt.production.ContractDocumentTypes;
import com.mwt.production.MediaProductionModel;
import com.mwt.production.ProductionDocumentModel;
import com.mwt.roles.ProductionRoleException;
import com.mwt.roles.ProductionRoleManager;
import com.mwt.roles.ProductionRoleModel;
import com.nvp.alfresco.docx.WordPropertiesManager;
import com.nvp.util.DocUtil;
import com.nvp.util.MapperUtil;
import com.nvp.util.NodeRefUtil;
   
public class ContractService {

	private ServiceRegistry registry;
	
	public static String CONTRACT_COMPONENT_ID = "contractContainer";
	public static String CONTRACT_CONTAINER_NAME = "Contracts";
    public static String CONTRACT_TEMPLATE_CONTAINER_NAME = "Contract Templates";
    
    public void setServiceRegistry(ServiceRegistry registry) {
        this.registry = registry;
       }
    
    
    /**
     * Utility function which provides the name of the contract document given the contract name as a string
     * 
     * @param contractName
     * @return
     * @throws ContractServiceException
     */
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
		 * site properties requires for the registeredOrg and the mediaProduction data
		 * 
		 */
		SiteInfo siteInfo = this.registry.getSiteService().getSite(siteName);
		Map<QName, Serializable> siteProperties = this.registry.getNodeService().getProperties(siteInfo.getNodeRef());
		 
		/**
		 * 
		 * set the production information into the prod:mediaProduction aspect
		 * 
		 */
		 Map<QName, Serializable> mediaProductionAspectProperties = new HashMap<QName, Serializable>();
		 MapperUtil.copyToMap(mediaProductionAspectProperties, MediaProductionModel.QN_PRODUCTION_NAME, siteProperties, MediaProductionModel.QN_PRODUCTION_NAME, "");
		 MapperUtil.copyToMap(mediaProductionAspectProperties, MediaProductionModel.QN_PRODUCTION_DESCRIPTION, siteProperties, MediaProductionModel.QN_PRODUCTION_DESCRIPTION, "");
		 MapperUtil.copyToMap(mediaProductionAspectProperties, MediaProductionModel.QN_PRODUCTION_ADDR1, siteProperties, MediaProductionModel.QN_PRODUCTION_ADDR1, "");
		 MapperUtil.copyToMap(mediaProductionAspectProperties, MediaProductionModel.QN_PRODUCTION_ADDR2, siteProperties, MediaProductionModel.QN_PRODUCTION_ADDR2, "");
		 MapperUtil.copyToMap(mediaProductionAspectProperties, MediaProductionModel.QN_PRODUCTION_ADDR3, siteProperties, MediaProductionModel.QN_PRODUCTION_ADDR3, "");
		 MapperUtil.copyToMap(mediaProductionAspectProperties, MediaProductionModel.QN_PRODUCTION_PO_CODE, siteProperties, MediaProductionModel.QN_PRODUCTION_PO_CODE, "");
		 
		 if( this.registry.getNodeService().hasAspect(contractDocumentNode, MediaProductionModel.QN_MEDIA_PRODUCTION_ASPECT)) {
			 this.registry.getNodeService().setProperties(contractDocumentNode, mediaProductionAspectProperties);
		 } else {
			 this.registry.getNodeService().addAspect(contractDocumentNode, MediaProductionModel.QN_MEDIA_PRODUCTION_ASPECT, mediaProductionAspectProperties);
		 }

		
		/**
		 * 
		 * set the 'contract:registeredOrg' aspect & properties in the node
		 * NOTE - this code should be moved to when we are ready to present the contract to the supplier
		 * 
		 */
		 Map<QName, Serializable> registeredOrgAspectProperties = new HashMap<QName, Serializable>();
		 MapperUtil.copyToMap(registeredOrgAspectProperties, RegisteredOrgModel.QN_REGISTERED_NAME, siteProperties, RegisteredOrgModel.QN_REGISTERED_NAME, "");
		 MapperUtil.copyToMap(registeredOrgAspectProperties, RegisteredOrgModel.QN_OPERATING_NAME, siteProperties, RegisteredOrgModel.QN_OPERATING_NAME, "");
		 MapperUtil.copyToMap(registeredOrgAspectProperties, RegisteredOrgModel.QN_FORMAL_ADDRESS, siteProperties, RegisteredOrgModel.QN_FORMAL_ADDRESS, "");
		 MapperUtil.copyToMap(registeredOrgAspectProperties, RegisteredOrgModel.QN_FORMAL_POST_CODE, siteProperties, RegisteredOrgModel.QN_FORMAL_POST_CODE, "");
		 

		 if( this.registry.getNodeService().hasAspect(contractDocumentNode, RegisteredOrgModel.QN_REGISTERED_ORG_MODEL)) {
			 this.registry.getNodeService().setProperties(contractDocumentNode, registeredOrgAspectProperties);
		 } else {
			 this.registry.getNodeService().addAspect(contractDocumentNode, RegisteredOrgModel.QN_REGISTERED_ORG_MODEL, registeredOrgAspectProperties);
		 }
		
		/**
		 * 
		 * set the 'prod:document' aspect & properties in the node
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

		 /**
		  * 
		  * set the 'contract:contractDocument' aspect & properties in the node
		  * 
		  */
		 Map<QName, Serializable> contractAspectProperties = new HashMap<QName, Serializable>();
		 contractAspectProperties.put(ContractDocumentModel.QN_CONTRACT_APPROVAL_STATUS, ContractDocumentModel.CONTRACT_STATUS_PREPARING);

		 if( this.registry.getNodeService().hasAspect(contractDocumentNode, ContractDocumentModel.QN_CONTRACT_ASPECT)) {
			 this.registry.getNodeService().setProperties(contractDocumentNode, contractAspectProperties);
		 } else {
			 this.registry.getNodeService().addAspect(contractDocumentNode, ContractDocumentModel.QN_CONTRACT_ASPECT, contractAspectProperties);
		 }
		 
		 
		 return contractDocumentNode;
		 
    }
    
    /**
     * merges the node metadata into the node content using Docx4J libraries.
     * Content must be a docx type
     * 
     */
    public void mergeContractPropertiesToContent(NodeRef contractNode, boolean saveAsPDF) throws ContractTemplateServiceException {
    
		/**
		 * 
		 * merge the contract document node properties into the contract documentNodement itself
		 * 
		 */
		Map<QName, Serializable> contractDocumentProperties = new HashMap<QName, Serializable>();
		NodeService nodeService = this.registry.getNodeService();
		contractDocumentProperties = nodeService.getProperties(contractNode);

		/**
		 * get an array of servicePeriod nodes
		 */
		Set<QName> servicePeriodTypeName = new HashSet<QName>();
		servicePeriodTypeName.add(ServicePeriodModel.QN_SERVICEPERIOD_TYPE);
		List<ChildAssociationRef> childRefs = nodeService.getChildAssocs(contractNode, servicePeriodTypeName);
		
		/**
		 * 
		 * build the XML to merge into the document
		 * 
		 */
		ContractContentBuilder contentBuilder = new ContractContentBuilder();
		try {
		contentBuilder.propertiesToCustomContent(contractDocumentProperties);
		if(!childRefs.isEmpty()) {
			Map<QName, Serializable> servicePeriodProperties = new HashMap<QName, Serializable>();
			for(ChildAssociationRef childAssoc : childRefs ) {
				servicePeriodProperties = nodeService.getProperties(childAssoc.getChildRef());
				
				String servicePeriodId = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICEPERIOD_ID);
				String servicePeriodName = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICEPERIOD_NAME); 
				String servicePeriodDesc = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICEPERIOD_DESCRIPTION); 
				String serviceTypeCode = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICE_TYPE_CODE);
				String servicePeriodType = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICE_PERIOD_TYPE);
				String serviceStart = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICE_START).toString(); 
				String serviceEnd = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICE_END).toString();
				
				        contentBuilder.addServicePeriod(servicePeriodId,
				        								servicePeriodName, 
				        								servicePeriodDesc, 
				        								serviceTypeCode, 
				        								servicePeriodType,
				        								serviceStart, 
				        								serviceEnd);
			}

		}
		
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			throw new ContractTemplateServiceException("Error creating the content to merge into the document", e1);
		} catch (TransformerException e2) {
			e2.printStackTrace();
			throw new ContractTemplateServiceException("Error creating the content to merge into the document", e2);
        } catch (Exception e3) {
        	e3.printStackTrace();
        	throw new ContractTemplateServiceException("Error creating the content to merge into the document", e3);
        }
		
		
		try {
			
		  WordPropertiesManager wordPropertiesManager = new WordPropertiesManager();
		  wordPropertiesManager.setServiceRegistry(this.registry);
	      wordPropertiesManager.setWordNodeRef(contractNode);
	      wordPropertiesManager.mergeProperties(contentBuilder.getDocument());
	      if(saveAsPDF) {
	        wordPropertiesManager.writeToNodeContentAsPDF(contractNode);
	      } else {
	    	wordPropertiesManager.writeToNodeContent(contractNode);  
	      }

          System.out.println("Contract document content update completed...");
        
		} catch (Exception e) {
		
			System.out.println(e);
			throw new ContractTemplateServiceException("Error merging the content into the document", e);
		}

    }
    
    
    /**
     * 
     * This status indicates that the contract is being prepared
     * 
     * @param nodeUUID
     */
    public void setStatusPreparing(String nodeUUID) throws ContractServiceException {
    	
    	setContractProperty(nodeUUID, ContractDocumentModel.QN_CONTRACT_APPROVAL_STATUS, ContractDocumentModel.CONTRACT_STATUS_PREPARING);
    	
    }

    /**
     * 
     * This status indicates that the contract has been prepared and is pending approval
     * 
     * @param nodeUUID
     */
    public void setStatusPendingApproval(String nodeUUID)  throws ContractServiceException {
    	
    	setContractProperty(nodeUUID, ContractDocumentModel.QN_CONTRACT_APPROVAL_STATUS, ContractDocumentModel.CONTRACT_STATUS_PENDING_APPROVAL);
    	
    }

    
    /**
     * 
     * This status indicates that the contract has been fully prepared and may be sent to the supplier/candidate
     * 
     * @param nodeUUID
     */
    public void setStatusPrepared(String nodeUUID) throws ContractServiceException {
    	
    	setContractProperty(nodeUUID, ContractDocumentModel.QN_CONTRACT_APPROVAL_STATUS, ContractDocumentModel.CONTRACT_STATUS_PREPARED);

    }

    /**
     * 
     * This status indicates that the contract is with the supplier for review
     * 
     * @param nodeUUID
     */
    public void setStatusSupplierReview(String nodeUUID) throws ContractServiceException {
    	
    	setContractProperty(nodeUUID, ContractDocumentModel.QN_CONTRACT_APPROVAL_STATUS, ContractDocumentModel.CONTRACT_STATUS_REVIEWING);

    }

    /**
     * 
     * This status indicates that the supplier has accepted the contract,
     * requires approval status to complete the contract acceptance process
     * 
     * This executes as admin since this status is set by 
     * the candidate who does not have access to the contract node
     * 
     * @param nodeUUID
     */
    public void setStatusSupplierAccepted(final String nodeUUID)  throws ContractServiceException {
    	
   	
		final ServiceRegistry localRegistry = this.registry;
		
		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            public Object doWork() throws Exception {

               ContractService.setContractProperty(localRegistry, nodeUUID, ContractDocumentModel.QN_CONTRACT_APPROVAL_STATUS, ContractDocumentModel.CONTRACT_STATUS_SUPPLIER_ACCEPTED); 	   
         	   return null;
         	   
            }
        });

    }
    
    /**
     * 
     * This status indicates that the supplier has declined the contract
     * 
     * This executes as admin since this status is set by 
     * the candidate who does not have access to the contract node
     * 
     * @param nodeUUID
     */
    public void setStatusSupplierDeclined(final String nodeUUID) throws ContractServiceException {
    	
		final ServiceRegistry localRegistry = this.registry;
		
		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            public Object doWork() throws Exception {

               ContractService.setContractProperty(localRegistry, nodeUUID, ContractDocumentModel.QN_CONTRACT_APPROVAL_STATUS, ContractDocumentModel.CONTRACT_STATUS_SUPPLIER_DECLINED); 	   
         	   return null;
         	   
            }
        });

    }
    
    /**
     * 
     * This status indicates that the contract has been approved after final checks.
     * This is an end state
     * 
     * @param nodeUUID
     */
    public void setStatusFinalApproved(String nodeUUID) throws ContractServiceException {
    	
    	setContractProperty(nodeUUID, ContractDocumentModel.QN_CONTRACT_APPROVAL_STATUS, ContractDocumentModel.CONTRACT_STATUS_FINAL_APPROVAL);
    	
    }
    
    /**
     * 
     * This status indicates that the contract has been declined after final checks.
     * This is an end state
     * 
     * @param nodeUUID
     */
    public void setStatusFinalRejected(String nodeUUID) throws ContractServiceException {
    	
    	setContractProperty(nodeUUID, ContractDocumentModel.QN_CONTRACT_APPROVAL_STATUS, ContractDocumentModel.CONTRACT_STATUS_FINAL_REJECTED);
    	
    }

    /**
     * 
     * This status indicates that the contract has been withdrawn.
     * This is an end state
     * 
     * @param nodeUUID
     */
    public void setStatusWithdrawn(String nodeUUID) throws ContractServiceException {
    	
    	setContractProperty(nodeUUID, ContractDocumentModel.QN_CONTRACT_APPROVAL_STATUS, ContractDocumentModel.CONTRACT_STATUS_WITHDRAWN);
    	
    }
    
    /**
     * sets a single property of a contract
     * 
     * @param nodeUUID
     * @param param
     * @param value
     * @throws ContractServiceException
     */
	public void setContractProperty(String nodeUUID, QName param, Serializable value) throws ContractServiceException {
		
		try {
			
			NodeRef roleNode = NodeRefUtil.NodeReffromUUID(nodeUUID);
			Map<QName, Serializable> newProperty = new HashMap<QName, Serializable>(1);
			newProperty.put(param, value);
			NodeRefUtil.mergeNodeProperties(roleNode, newProperty, this.registry.getNodeService());
			
		} catch (Exception e) {
			
			throw new ContractServiceException("ERROR: Unable to set property for contract node [" + nodeUUID + "]", e);
			
		}
	}

    /**
     * sets a single property of a contract
     * 
     * @param nodeUUID
     * @param param
     * @param value
     * @throws ContractServiceException
     */
	public static void setContractProperty(ServiceRegistry registry, String nodeUUID, QName param, Serializable value) throws ContractServiceException {
		
		try {
			
			NodeRef roleNode = NodeRefUtil.NodeReffromUUID(nodeUUID);
			Map<QName, Serializable> newProperty = new HashMap<QName, Serializable>(1);
			newProperty.put(param, value);
			NodeRefUtil.mergeNodeProperties(roleNode, newProperty, registry.getNodeService());
			
		} catch (Exception e) {
			
			throw new ContractServiceException("ERROR: Unable to set property for contract node [" + nodeUUID + "]", e);
			
		}
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

		   // Contracts folder
		   contractsContainer = fileFolderService.create(docLibContainer, 
                   CONTRACT_CONTAINER_NAME, 
                   ContractDocumentModel.QN_CONTRACT_CONTAINER_TYPE).getNodeRef();

		   Map<QName, Serializable> containerProperties = new HashMap<QName, Serializable>();
		   containerProperties.put(SiteModel.PROP_COMPONENT_ID, ContractDocumentModel.CONTRACT_CONTAINER_ID);
		   nodeService.addAspect(contractsContainer , SiteModel.ASPECT_SITE_CONTAINER, containerProperties);

		   // Contracts template
		   contractsContainer = fileFolderService.create(docLibContainer, 
                   CONTRACT_TEMPLATE_CONTAINER_NAME, 
                   ContractDocumentModel.QN_CONTRACT_CONTAINER_TYPE).getNodeRef();

		   

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
