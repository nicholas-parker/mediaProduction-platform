package com.mwt.production;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;

public class ProductionManagementService {

	private ServiceRegistry registry;
	
    public void setServiceRegistry(ServiceRegistry registry) {
        this.registry = registry;
    }

	public Map<QName, Serializable> getProductionProperties(String productionShortName) {
		
		SiteService siteService = this.registry.getSiteService();
		SiteInfo siteInfo = siteService.getSite(productionShortName);
		return this.registry.getNodeService().getProperties(siteInfo.getNodeRef());
		
	}
	
	public String getAdminEmail(String productionShortName) {
		
		return "productionManager@mwt.com";
		
	}
	
	public String getProductionName(String productionShortName) {
		
		Map<QName, Serializable> props = getProductionProperties(productionShortName);
		if(props.containsKey(ContentModel.PROP_NAME)) {
			return props.get(ContentModel.PROP_NAME).toString();
		} else {
			return null;
		}
	}
}
