package com.mwt.activiti.handlers.roleContract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.activiti.WorkflowUtil;
import com.mwt.contract.model.ContractCrewEngagementModel;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.contract.model.ContractFormsModel;
import com.mwt.contract.model.INdividu;
import com.mwt.roles.DefaultRoleModel;
import com.mwt.roles.ProductionRoleModel;
import com.nvp.util.MapperUtil;
import com.nvp.util.NodeRefUtil;

public class AddContractToWorkflow  extends AbstractAlfrescoListener implements ExecutionListener {

	private static String bpm_package = "bpm_package";
	
	/**
	 * Called from workflow after the start form.
	 * Promotes all the properties of the contract node into the workflow
	 * Creates an association between the workflow package and the contract document
	 * 
	 * @throws Exception 
	 * 
	 */
	public void notify(DelegateExecution execution) throws Exception {
	
		NodeService nodeService = this.getServiceRegistry().getNodeService();
		
		/** contractId: node id for contract to add to workflow package */
		String contractId = (String) execution.getVariableLocal("contract_contractDocumentNodeId");
		if( null == contractId) {
			throw new Exception("Cannot start process, process variable 'contractId' is null");
		}
		
		/** get contract properties and promote into workflow as workflow properties */
		NodeRef contractNodeRef = NodeRefUtil.NodeReffromUUID(contractId);
		Map<QName, Serializable> contractProperties = nodeService.getProperties(contractNodeRef);
		MapperUtil util = new MapperUtil();
		util.setNamespaceService(this.getServiceRegistry().getNamespaceService());
		Map<String,String> workflowVars = util.QNameListToFlatMap(contractProperties);
		execution.setVariables(workflowVars);
		
		/** put the vars on log file to help debugging */
		System.out.println("Starting contract management workflow, process " + execution.getProcessInstanceId() + ", adding these variables from the contract node");
		Iterator it = workflowVars.entrySet().iterator();
		while(it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, String> entry = (Map.Entry<String, String>)it.next();
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		

		NodeRef workflowPackageNode = null;
		if(execution.hasVariable(bpm_package)) {
			
			ActivitiScriptNode n = (ActivitiScriptNode) execution.getVariable(bpm_package);
			workflowPackageNode = n.getNodeRef();
			if(NodeRef.isNodeRef( workflowPackageNode.toString()) && nodeService.exists(workflowPackageNode)) {
	
			} else {
				System.out.print("ERROR Process [" + execution.getProcessInstanceId().toString() + "] cant make a contract document because bpm_workflowPackage [" + execution.getVariable(bpm_package).toString() + "] is not a valid nodeRef");
				return;
			}
			
		} else {
			
			
			System.out.print("WARNING didn't create a contract document for process " + execution.getProcessInstanceId().toString() + " because it had no associated contract template");
			return;
		}
		
		/**
		 * 
		 * create child relationship from workflow package 
		 * 
		 */
		 try {
			  String contractName = (String) contractProperties.get(ContentModel.PROP_NAME);  
			  nodeService.addChild(workflowPackageNode, 
		               NodeRefUtil.NodeReffromUUID(contractId), 
		               WorkflowModel.ASSOC_PACKAGE_CONTAINS,
		               QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, 
		            		             QName.createValidLocalName(contractName)));
		  
		  } catch (Exception e) {
			  
			  System.out.println("Unable to add new contract document to workflow package, " + e.getMessage());
			  throw e;
		  }
		 
		
	    
	}

}
