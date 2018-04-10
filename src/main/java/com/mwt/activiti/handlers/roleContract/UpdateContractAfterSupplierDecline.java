package com.mwt.activiti.handlers.roleContract;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.contract.ContractService;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.roles.ProductionRoleManager;
import com.nvp.util.NodeRefUtil;

/**
 * 
 * This listener is called as a service task.
 * 
 * Updates a contract and role after the position has been declined by a candidate
 * 
 * - set the role status to declined
 * - set the contract status to declined
 * 
 * @author nick
 *
 */
public class UpdateContractAfterSupplierDecline extends AbstractAlfrescoListener implements JavaDelegate {
	
	
	public static String ROLE_NODE = "nvpList_roleNodeRef";
	
	@Override
	public void execute(DelegateExecution exec) throws Exception {
	
	
		/**
		 * 
		 * check we have a role node and a contract node
		 * 
		 */
		if(this.hasValue(ROLE_NODE, exec) == Boolean.FALSE) {
			
			throw new RoleContractException("Unable to update role node status after supplier decline as ROLE_NODE property is not in workflow process " + exec.getProcessInstanceId());
		}
		String roleUUID = NodeRefUtil.UUIDfromURL( exec.getVariable(ROLE_NODE).toString() );

		if(this.hasValue(ContractDocumentModel.CONTRACT_DOCUMENT_NODE_ID, exec) == Boolean.FALSE) {
			
			throw new RoleContractException("Unable to update role node status after supplier decline as CONTRACT_NODE property is not in workflow process " + exec.getProcessInstanceId());
		}
		String contractUUID = exec.getVariable(ContractDocumentModel.CONTRACT_DOCUMENT_NODE_ID).toString();
		
		/**
		 * 
		 * set the contract status to declined
		 * 
		 */
		try {
			
			ContractService contractService = new ContractService();
			contractService.setServiceRegistry(this.getServiceRegistry());
			contractService.setStatusSupplierDeclined(contractUUID);
			
		} catch (Exception e) {
			
			RoleContractException rce = new RoleContractException("Error whilst updating the contract status to declined, " + e.getMessage(), e);
			rce.printStackTrace();
			throw rce;
			
		}

		/**
		 * 
		 * set the role status to declined
		 * 
		 */
		try {
			
			ProductionRoleManager roleManager = new ProductionRoleManager();
			roleManager.setServiceRegistry(this.getServiceRegistry());
			roleManager.setStatusDeclined(roleUUID);
			
		} catch (Exception e) {
			
			RoleContractException rce = new RoleContractException("Error whilst updating the role status to declined, " + e.getMessage(), e);
			rce.printStackTrace();
			throw rce;
			
		}

	}

}
