package com.mwt.activiti.handlers.roleContract;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.JavaDelegate;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.crew.CrewService;
import com.nvp.util.DocUtil;
import com.nvp.util.NodeRefUtil;

public class CreateCrewMemberFolder extends AbstractAlfrescoListener implements JavaDelegate {

	/**
	 * 
	 * This listener should be invoked when the crew member is assured a place on the team.
	 * The listener creates a folder for the crew member and copies all the workflow
	 * documents into the folder
	 * 
	 */
	@Override
	public void execute(DelegateExecution exec) throws Exception {
		
		final String site = "site";
		/**
		 * 
		 * check we know which production we are working on
		 * 
		 */
		if(!exec.hasVariable(site) || exec.getVariable(site).toString().isEmpty()) {
			
			System.out.println("ERROR: Unable to create a folder for site member because we have no site variable in the process");
			return;
		}
		
		
		/**
		 * 
		 * check we know the username of the new member
		 * 
		 */
		final String userName = "contract_contractSupplier";
        if(!exec.hasVariable(userName) || exec.getVariable(userName).toString().isEmpty()) {
			
			System.out.println("ERROR: Unable to create a folder for site member because we have no username variable in the process");
			return;
			
		}
        
        /**
         *
         *  get reference to the crew members folder in the site's Crew folder
         */
        CrewService crewService = new CrewService();
        crewService.setServiceRegistry(this.getServiceRegistry());

        NodeRef crewFolder = crewService.getSiteCrewMemberFolder(exec.getVariable(userName).toString(), 
        		                                                 exec.getVariable(site).toString());
        
        /**
         * 
         * copy all the documents in the workflow package to the crewmember's folder
         * 
         */
        final String bpm_package = "bpm_package";
        if(!exec.hasVariable(bpm_package) || exec.getVariable(bpm_package).toString().isEmpty()) {
        	
			System.out.println("ERROR: Unable to copy documents into member's folder because there is no bpm_package in this process");
			return;

        }
        
        NodeService nodeService = this.getServiceRegistry().getNodeService();
        NodeRef packageRef = NodeRefUtil.NodeReffromBPMPackage(exec);
        
        List<ChildAssociationRef> children = nodeService.getChildAssocs(packageRef);
        for(ChildAssociationRef childRef : children) {
        	
        	// QName childType = childRef.getTypeQName();
        	// if( childType == ContentModel.TYPE_CONTENT) {
 
        		try {
        			
        		  NodeRef childNode = childRef.getChildRef();
        		  Map<QName, Serializable> props = nodeService.getProperties(childNode);
        		  String name = props.get(ContentModel.PROP_NAME).toString();
        		  DocUtil.createDocumentFromOriginal(name, childNode, crewFolder, this.getServiceRegistry());
        		
        		} catch (Exception e) {
        		
        		  System.out.println("ERROR: error copying document from workflow package into crew folder," + e.getMessage());
        		  
        		}
        	// }
        }
	}

}
