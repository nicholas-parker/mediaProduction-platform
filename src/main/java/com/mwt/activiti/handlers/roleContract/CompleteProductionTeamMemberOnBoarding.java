package com.mwt.activiti.handlers.roleContract;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.JavaDelegate;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.contract.ContractService;
import com.mwt.contract.model.ContractCrewEngagementModel;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.crew.CrewService;
import com.mwt.roles.ProductionRoleManager;
import com.mwt.roles.ProductionRoleModel;
import com.nvp.util.DocUtil;
import com.nvp.util.NodeRefUtil;

public class CompleteProductionTeamMemberOnBoarding extends AbstractAlfrescoListener implements JavaDelegate {

	/**
	 * 
	 * This listener completes the member on boarding process.
	 * Invoked after the right to work is confirmed, right to work being last manual step
	 * 
	 * Actions are
	 * - create a folder for the crew member and copies all the workflow documents into the folder
	 * - adds the user as a production crew member with associated rights
	 * - notifies the new crew member that right to work has been approved
	 * - sets the status of the contract to complete
	 * 
	 */
	
	private static String ROLE_NODE_REF = "nvpList_roleNodeRef";
	
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
         *  add the crew member to the production and
         *  get reference to the crew members folder in the site's Crew folder
         */
        CrewService crewService = new CrewService();
        crewService.setServiceRegistry(this.getServiceRegistry());

        NodeRef crewFolder = crewService.addNewCrewToSite(exec.getVariable(userName).toString(), 
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

        		try {
        			
        		  NodeRef childNode = childRef.getChildRef();
        		  Map<QName, Serializable> props = nodeService.getProperties(childNode);
        		  String name = props.get(ContentModel.PROP_NAME).toString();
        		  DocUtil.createDocumentFromOriginal(name, childNode, crewFolder, this.getServiceRegistry());
        		
        		} catch (Exception e) {
        		
        		  System.out.println("ERROR: error copying document from workflow package into crew folder," + e.getMessage());
        		  
        		}

        }
        
        
        /**
         * 
         * set the contract status to FINAL_COMPLETE, an end state.
         * set the role status to approved.
         * 
         */
        String contractNodeUUID = null;
        if( this.hasValue(ContractDocumentModel.CONTRACT_DOCUMENT_NODE_ID, exec)) {
            
        	/** contract status */
        	try {
       
        		contractNodeUUID = exec.getVariable(ContractDocumentModel.CONTRACT_DOCUMENT_NODE_ID).toString();
        		ContractService contractService = new ContractService();
            	contractService.setServiceRegistry(this.getServiceRegistry());
            	contractService.setStatusFinalApproved(contractNodeUUID);
            
        	} catch (Exception e) {
        		
        		e.printStackTrace();
        		throw e;
        		
        	}
        	
        	/** role status */
           	try {
                
           		NodeRef contractNode = NodeRefUtil.NodeReffromUUID(contractNodeUUID);
           		QName sourceName = ProductionRoleModel.QN_ASSOC_ROLE_CONTRACT_DOCUMENTS;
           		List<AssociationRef> role = nodeService.getSourceAssocs(contractNode, sourceName);
           		if(role.size() > 0) {
           		
           			NodeRef roleNode = role.get(0).getSourceRef();
           			String nodeUUID = roleNode.getId();
           			ProductionRoleManager productionRoleManager = new ProductionRoleManager();
                	productionRoleManager.setServiceRegistry(this.getServiceRegistry());
                	productionRoleManager.setStatusApproved(nodeUUID);
                	
           		}
        		
        		
            
        	} catch (Exception e) {
        		 
        		e.printStackTrace();
        		throw e;

            	// RoleContractException e =  new RoleContractException("Unable to set role status to complete as process " +  exec.getProcessInstanceId().toString() + " does not have a valid " + ROLE_NODE_REF);
            	// e.printStackTrace();
            	// throw e;

        	}

        	
        } else {
        	
        	RoleContractException e =  new RoleContractException("Unable to set role status to complete as process " +  exec.getProcessInstanceId().toString() + " does not have a valid " + ContractDocumentModel.CONTRACT_DOCUMENT_NODE_ID);
        	e.printStackTrace();
        	throw e;
        }
        
        /**
         * set the role status to complete, the role is found from the contract source association
         * 
         */
        	

        	

        	
	}

}
