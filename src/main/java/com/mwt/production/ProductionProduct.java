package com.mwt.production;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteService;

import com.mwt.contract.ContractService;
import com.mwt.contract.ContractServiceException;
import com.mwt.crew.CrewService;
import com.mwt.crew.CrewServiceException;
import com.mwt.roles.DefaultRoleModel;
import com.mwt.roles.ProductionRoleModel;
import com.nvp.alfresco.datalist.DataListUtil;

public class ProductionProduct {

	/**
	 * Creates the standard set of items for a Production.  These are
	 * 
	 * - Crew folder in DocumentLibrary
	 * - Contracts folder in DocumentLibrary
	 * - DefaultRole list in DataList, but does not populatet the list
	 * - ProductionRole list in DataList
	 * 
	 * 
	 */
	public void addStandardProduct(String siteShortName, ServiceRegistry registry) {
		
		SiteService siteService = registry.getSiteService();
		NodeRef docLibNode = siteService.getContainer(siteShortName, "documentLibrary");
		
		/**
		 * 
		 * create the 'Crew' folder
		 * 
		 */
		try {
		
			CrewService crewService = new CrewService();
			crewService.setServiceRegistry(registry);
			crewService.createCrewFolder(siteShortName);
		
		} catch (CrewServiceException cse ) {
			
			System.out.println("ERROR: Unable to create crew service folder when setting up new site [" + siteShortName + "], carrying on");
			
		}
		/**
		 * 
		 * create the 'Contracts' folder & 'Contracts Template' folder
		 * 
		 */
		try {
			
			ContractService contractService = new ContractService();
			contractService.setServiceRegistry(registry);
			contractService.createContractContainer(siteShortName);

		} catch (ContractServiceException cse) {
			
			System.out.println("ERROR: Unable to create contracts folder when setting up new site [" + siteShortName + "], carrying on");
			
		}
		
		/**
		 * 
		 * create the DefaultRoles & Production Roles data list
		 * 
		 */
		try {
		
			DataListUtil dataListUtil = new DataListUtil(registry.getSiteService(), registry.getNodeService());
			dataListUtil.createDataFolder(siteShortName, DefaultRoleModel.DefaultRoleListName, DefaultRoleModel.defaultRoleTypeName);
			dataListUtil.createDataFolder(siteShortName, ProductionRoleModel.RoleListName  , ProductionRoleModel.productionRoleTypeName);
		
		} catch ( Exception e) {
			
			System.out.println("ERROR: Unable to create defaultRole or productionRole folder when setting up new site [" + siteShortName + "], carrying on");
			
		}
	}
	
	
}
