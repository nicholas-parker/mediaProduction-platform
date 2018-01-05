package com.mwt.contract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;

import com.mwt.contract.model.ContractTemplateModel;
import com.mwt.crew.CrewServiceException;
import com.mwt.crew.model.CrewModel;

public class ContractTemplateService {

	private ServiceRegistry registry;
	
    public void setServiceRegistry(ServiceRegistry registry) {
        this.registry = registry;
    }

	/**
	 * 
	 * @param siteName
	 * @return
	 */
	NodeRef getContractTemplateFolder(String siteShortName) throws ContractTemplateServiceException {
		
		SiteService siteService = registry.getSiteService();
		FileFolderService fileFolderService = registry.getFileFolderService();
		NodeService nodeService = registry.getNodeService();
		SearchService searchService = registry.getSearchService();

		NodeRef contractTemplateContainer = null;
		NodeRef docLibContainer = siteService.getContainer(siteShortName, SiteService.DOCUMENT_LIBRARY);
		if(docLibContainer == null) {
			throw new ContractTemplateServiceException("No document library in this site " + siteShortName, null); }

		try {

			List<String> paths = new ArrayList<String>(1);
			paths.add(ContractTemplateModel.CONTRACT_CONTAINER_NAME);
			FileInfo fileInfo = fileFolderService.resolveNamePath(docLibContainer, paths);
			if (!fileInfo.isFolder())
			{
				throw new ContractTemplateServiceException("ERROR: looking for contract template container in site " + siteShortName + ", but the item is not a foolder", null);
			}
			contractTemplateContainer = fileInfo.getNodeRef();

		} catch( FileNotFoundException fnfe) {

			/*
			 * 
			 * we are here because we didn't find a contracts container so create one
			 * 
			 */
			contractTemplateContainer = fileFolderService.create(docLibContainer,
					ContractTemplateModel.CONTRACT_CONTAINER_NAME,
					ContractTemplateModel.QN_CONTRACT_CONTAINER_TYPE).getNodeRef();

			Map<QName, Serializable> containerProperties = new HashMap<QName, Serializable>();
			containerProperties.put(SiteModel.PROP_COMPONENT_ID, ContractTemplateModel.CONTRACT_CONTAINER_ID);
			nodeService.addAspect(contractTemplateContainer , SiteModel.ASPECT_SITE_CONTAINER, containerProperties);


		} catch( Exception e) {

			ContractTemplateServiceException cse = new ContractTemplateServiceException("ERROR: very unexpected thing happened whilst searching for contracts container, " + e.getMessage(), null);
			cse.printStackTrace();
			throw cse;

		}

		return contractTemplateContainer;

	}
	
	/**
	 * Creates the contract template folder under the documentLibrary folder
	 * 
	 * @throws CrewServiceException
	 */
	public NodeRef createContractTemplateFolder(String siteShortName) throws CrewServiceException {
		
		SiteService siteService = registry.getSiteService();
		FileFolderService fileFolderService = registry.getFileFolderService();
		NodeService nodeService = registry.getNodeService();
		
		NodeRef contractTemplateContainer = null;
		try {
			
			NodeRef docLibContainer = siteService.getContainer(siteShortName, SiteService.DOCUMENT_LIBRARY);
			contractTemplateContainer = fileFolderService.create(docLibContainer,
					ContractTemplateModel.CONTRACT_CONTAINER_NAME,
					ContractTemplateModel.QN_CONTRACT_CONTAINER_TYPE).getNodeRef();

			Map<QName, Serializable> containerProperties = new HashMap<QName, Serializable>();
			containerProperties.put(SiteModel.PROP_COMPONENT_ID, ContractTemplateModel.CONTRACT_CONTAINER_ID);
			nodeService.addAspect(contractTemplateContainer , SiteModel.ASPECT_SITE_CONTAINER, containerProperties);
		
		} catch (Exception e) {

			CrewServiceException cse = new CrewServiceException("ERROR: very unexpected thing happened whilst creaeting crew container in site  [" + siteShortName + "], " + e.getMessage(), null);
			cse.printStackTrace();
			throw cse;
		}
		
		return contractTemplateContainer;
	}
	
	/**
	 * 
	 * Returns a list of node ref for all the nodes in the sites Contract Template folder
	 * 
	 * @param siteShortName
	 * @return a list of node ref which represent the contract templates for the given site
	 * @throws ContractTemplateServiceException 
	 * 
	 * 
	 */
	List<NodeRef> getProductionContractTemplates(String siteShortName) throws ContractTemplateServiceException {
		
		NodeRef contractTemplateFolder = this.getContractTemplateFolder(siteShortName);
		List<NodeRef> nodes = new ArrayList<NodeRef>();
		List<ChildAssociationRef> childRefs = this.registry.getNodeService().getChildAssocs(contractTemplateFolder);
		for(ChildAssociationRef child : childRefs) {
			nodes.add(child.getChildRef());
		}
		return nodes;
	}

}
