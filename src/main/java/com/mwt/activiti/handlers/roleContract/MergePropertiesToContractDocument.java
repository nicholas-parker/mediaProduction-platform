package com.mwt.activiti.handlers.roleContract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.production.ContractDocumentTypes;
import com.nvp.alfresco.docx.WordPropertiesManager;
import com.nvp.util.DocUtil;

/**
 * 
 * Updates the contact document with the current role information
 * 
 * This class is called by the role-management-process once the role details have been confirmed
 * The properties are extracted from the workflow and updated on the contract document meta data
 * The meta data is then pushed into the document and the document content re-rendered
 * 
 * @author nick
 *
 */
public class MergePropertiesToContractDocument extends AbstractAlfrescoListener implements TaskListener{

	/**
	 * 
	 * the name of the workflow property which contains the document package node ref
	 * 
	 */
	private static String bpm_package = "bpm_package";
	
	/**
	 * 
	 * 
	 */
	@Override
	public void notify(DelegateTask task) {
		
		DelegateExecution exec = task.getExecution();
		
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
		DocUtil.mergeContractDocumentAspect(exec, contractDocs.get(0), this.getServiceRegistry());
		DocUtil.mergeServicePeriodAspect(exec, contractDocs.get(0), this.getServiceRegistry());
		DocUtil.mergeIndividualSupplierAspect(exec, contractDocs.get(0), this.getServiceRegistry());
		
		/*
		 * 
		 * merge the contract document node properties into the contract docudocumentNodement itself
		 * 
		 */
		// WordPropertiesManager.mergePropertiesToDocument(contractDocs.get(0), this.getServiceRegistry());
		Map<QName, Serializable> wordProperties = new HashMap<QName, Serializable>();
		NodeService nodeService = this.getServiceRegistry().getNodeService();
		wordProperties = nodeService.getProperties(contractDocs.get(0));
		
		try {
		
			
			
			
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
