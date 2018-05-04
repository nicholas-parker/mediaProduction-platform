package com.mwt.activiti.handlers.roleContract;

import java.io.Serializable;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.activiti.WorkflowUtil;
import com.mwt.roles.ProductionRoleModel;
import com.nvp.util.NodeRefUtil;

public class SetContractManagementProcessGroup extends AbstractAlfrescoListener implements ExecutionListener {

	
	/**
	 * Called from workflow after the start form.
	 * Sets the site group which will manage the workflow process
	 * 
	 * @throws Exception 
	 * 
	 */
	public void notify(DelegateExecution arg0) throws Exception {
	
		/** mwt_site: site name */
		String siteName = (String) arg0.getVariableLocal("site");
		if( null == siteName) {
			throw new Exception("Cannot start process, process variable 'site' is null");
		}

		/** set the group which will administer this process */
	    String defaultRoleManagementTeam = "Contracts admin team";
	    String groupVariable = "contract_contractApprovalRole";
	    String groupName = WorkflowUtil.getWorkflowAdminGroup(defaultRoleManagementTeam, siteName);
	    System.out.println("SetProcessGroup: assigning workflow " + arg0.getProcessDefinitionId() + " / " + arg0.getProcessInstanceId() + " to group " + groupName);
	    arg0.setVariableLocal(groupVariable,groupName);

	    /** write the processId back to the role **/
	    String roleId = (String) arg0.getVariableLocal("nvpList_roleId");
	    if(null != roleId) {
	    	
	    	NodeService nodeService = this.getServiceRegistry().getNodeService();
	    	NodeRef roleNodeRef = NodeRefUtil.NodeReffromUUID(roleId);
	    	Map<QName, Serializable> roleProperties = nodeService.getProperties(roleNodeRef);
	    	roleProperties.put(ProductionRoleModel.QN_ROLE_STATUS, ProductionRoleModel.ROLE_STATUS_SETUP);
	    	roleProperties.put(ProductionRoleModel.QN_CONTRACT_PROCESS_ID, arg0.getProcessInstanceId());
	        nodeService.setProperties(roleNodeRef, roleProperties);
	        
	    }
	    
	}

}
