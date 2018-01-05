package com.mwt.activiti.handlers.roleContract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.contract.activiti.ApplyRightToWorkAspectFromProcess;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.production.ContractDocumentTypes;
import com.mwt.roles.ProductionRoleException;
import com.mwt.roles.ProductionRoleManager;
import com.mwt.roles.ProductionRoleModel;
import com.nvp.alfresco.docx.WordPropertiesManager;
import com.nvp.util.DocUtil;
import com.nvp.util.MapperUtil;
import com.nvp.util.NodeRefUtil;

public class UpdateContractAfterSupplierReview extends AbstractAlfrescoListener implements TaskListener {

	/**
	 * 
	 * the name of the workflow property which contains the document package node ref
	 * 
	 */
	private static String bpm_package = "bpm_package";
	
	/**
	 * 
	 * the name of the workflow property which contains the review outcome
	 * 
	 */
	private static String reviewStatusOutcome = "reviewStatusOutcome";
	
	/**
	 * 
	 * the name of the workflow property which contains the node UUID of the crew role we are managing
	 * 
	 */
	private static String roleNodeRef = "nvpList_roleNodeRef";
	
	/**
	 * 
	 * 
	 */
	@Override
	public void notify(DelegateTask task) {
		
		DelegateExecution exec = task.getExecution();

		/**
		 * 
		 * set the review outcome status to accept or declined
		 * 
		 */
		if(task.hasVariable(reviewStatusOutcome)) {
		
			String reviewOutcome = task.getVariable(reviewStatusOutcome).toString();
			
			String roleNodeUUID = null;
			if(task.hasVariable(roleNodeRef)) {
			
				roleNodeUUID = NodeRefUtil.UUIDfromURL( task.getVariable(roleNodeRef).toString() );
				
			} else {
				
				System.out.println("ERROR: unable to update status of role as this process doesnt contain a roleNodeRef property");
				
			}
			
			ProductionRoleManager roleManager = new ProductionRoleManager();
			roleManager.setServiceRegistry(this.getServiceRegistry());
			
			try {
			if(reviewOutcome.equals(ProductionRoleModel.ROLE_STATUS_ACCEPTED)) {
				roleManager.setRoleAccepted(roleNodeUUID);
		
			}
			
			if(reviewOutcome.equals(ProductionRoleModel.ROLE_STATUS_DECLINED)) {
				roleManager.setRoleDeclined(roleNodeUUID);
				return;
			}
			
			} catch(ProductionRoleException e) {
				
				System.out.println("ERROR: Unable to update the role status. " + e.getMessage());
				
			}
		
		} else {
			
			System.out.println("ERROR: workflow did not contain an outcome property, unable to update role status");
			
		}

		/**
		 * 
		 * get the Employment contract in the workflow package
		 * 
		 */
		List<NodeRef> contractDocs = null;
		if(task.hasVariable(bpm_package)) {

			ActivitiScriptNode n = (ActivitiScriptNode) exec.getVariable(bpm_package);
			contractDocs = DocUtil.findDocumentByType(n.getNodeRef(), ContractDocumentTypes.EmploymentContract, this.getServiceRegistry());
        
			if(null == contractDocs || contractDocs.isEmpty()) {
				
				System.out.print("WARNING Attempting to update contract document in workflow " + exec.getProcessInstanceId() + " but didnt find an Employment contract in the workflow package");
				return;
				
			}
			
		} else {

			System.out.print("WARNING Attempting to update contract document in workflow " + exec.getProcessInstanceId() + " but the workflow had no package");
			return;

		}
		
		
		/**
		 * 
		 * merge the workflow properties into the employment contract document node
		 * 
		 */
		DocUtil.mergeIndividualSupplierAspect(exec, contractDocs.get(0), this.getServiceRegistry());
		DocUtil.mergeBankAccountAspect(exec, contractDocs.get(0), this.getServiceRegistry());
		ApplyRightToWorkAspectFromProcess.Merge(exec, contractDocs.get(0), this.getServiceRegistry());
		
		/*
		 * 
		 * merge the contract document node properties into the contract docudocumentNodement itself
		 * 
		 */
		try {
			
			// WordPropertiesManager.mergePropertiesToDocument(contractDocs.get(0), this.getServiceRegistry());
			Map<QName, Serializable> wordProperties = new HashMap<QName, Serializable>();
			 NodeService nodeService = this.getServiceRegistry().getNodeService();
			 wordProperties = nodeService.getProperties(contractDocs.get(0));
			 
			WordPropertiesManager wordPropertiesManager = new WordPropertiesManager();
			wordPropertiesManager.setServiceRegistry(this.getServiceRegistry());
		    wordPropertiesManager.setWordNodeRef(contractDocs.get(0));
		    wordPropertiesManager.mergeProperties(wordProperties);
		    wordPropertiesManager.writeToNodeContent(contractDocs.get(0));
		 

		} catch (Exception e) {
			
			System.out.println(e);
		
		}
		
	}

}
