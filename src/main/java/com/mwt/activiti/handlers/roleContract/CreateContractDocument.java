package com.mwt.activiti.handlers.roleContract;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.persistence.entity.VariableInstanceEntity;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.contract.ContractService;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.contract.model.INdividu;
import com.mwt.production.ContractDocumentTypes;
import com.mwt.production.ProductionDocumentModel;
import com.mwt.roles.ProductionRoleModel;
import com.nvp.alfresco.docx.WordPropertiesManager;
import com.nvp.util.DocUtil;

/**
 * Java service task delegate which will 
 *  - create the Contract document from a template
 *  - add the apropriate apsects and set the document type
 *  - merge the workflow properties into the document
 *  - associate the document with the workflow
 *  
 * @author nick
 *
 */
public class CreateContractDocument extends AbstractAlfrescoListener implements ExecutionListener {

	private static String nvpList_contractTemplate = "nvpList_contractTemplate";
	private static String bpm_package = "bpm_package";
    private static String site = "site";
	private static String storeType = "workspace";
	private static String storeId = "SpacesStore";
	
	private static String nvpList_roleName = "nvpList_roleName";
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		
		NodeService nodeService = this.getServiceRegistry().getNodeService();
		
		/**
		 * 
		 * Create a contract document from the template and place it in the workflowPackage folder
		 * 
		 * For this to happen the process must have 
		 * 1 - a variable named 'nvpList:contractTemplate'
		 * 2 - a variable named 'bpm:workflowPackage'
		 * 
		 */
		NodeRef contractTemplateNode = null;
		if(execution.hasVariable(nvpList_contractTemplate)) {
			
			
			contractTemplateNode = new NodeRef(storeType, storeId, execution.getVariable(nvpList_contractTemplate).toString());
			if(NodeRef.isNodeRef( contractTemplateNode.toString()) && nodeService.exists(contractTemplateNode)) {
					
			} else {
				System.out.print("ERROR Process [" + execution.getProcessInstanceId().toString() + "] cant make a contract document because nvpList_contractTemplate [" + execution.getVariable(nvpList_contractTemplate).toString() + "] is not a valid nodeRef");
				return;
			}
			
		} else {
			
			
			System.out.print("WARNING didn't create a contract document for process " + execution.getProcessInstanceId().toString() + " because it had no associated contract template");
			return;
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
		 * create the contract document from the template and store in the site contract folder
		 * 
		 */
		 if(!execution.hasVariable(nvpList_roleName)) {
			 
			 System.out.println("Unable to create contract because process does not contain nvpList_roleName variable");
			 return;
		 }
		 
         if(!execution.hasVariable(site)) {
			 
			 System.out.println("Unable to create contract because process does not contain site variable");
			 return;
		 }
		 
		 ContractService contractService = new ContractService();
		 contractService.setServiceRegistry(this.getServiceRegistry());
		 String contractDocumentName = contractService.createContractDocumentFullFilename(execution.getVariable(nvpList_roleName).toString());
		 NodeRef contractDocumentNode = contractService.createContractForProduction(execution.getVariable(site).toString(), 
				                                                                    contractDocumentName,
				                                                                    null,
				                                                                    contractTemplateNode);
		 
	     /**
		  * 
		  * create child relationship from workflow package 
		  * 
		  */
		  try {
			  
			  nodeService.addChild(workflowPackageNode, 
		               contractDocumentNode, 
		               WorkflowModel.ASSOC_PACKAGE_CONTAINS,
		               QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, 
		            		             QName.createValidLocalName(contractDocumentName)));
		  
		  } catch (Exception e) {
			  
			  System.out.println("Unable to add new contract document to workflow package, " + e.getMessage());
			  throw e;
		  }
		   
	}

	
}
