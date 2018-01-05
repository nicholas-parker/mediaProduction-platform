package com.mwt.activiti.handlers.roleContract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.ExecutionListener;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.invitation.Invitation;
import org.alfresco.service.cmr.invitation.InvitationService;
import org.alfresco.service.cmr.invitation.NominatedInvitation;
import org.alfresco.service.namespace.QName;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.activiti.WorkflowUtil;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.contract.model.ContractFormsModel;
import com.mwt.contract.model.INdividu;
import com.mwt.roles.DefaultRoleModel;
import com.mwt.roles.ProductionRoleManager;
import com.mwt.roles.ProductionRoleModel;
import com.nvp.util.MapperUtil;

/**
 * Creates a Role entity in the site/datalist/role folder
 * Uses the provided defaultRole to set the role values
 * Creates a role contract document and attaches to this workflow
 * 
 * @author Nick
 *
 */
public class CreateRoleFromDefault extends AbstractAlfrescoListener implements ExecutionListener {

	
	/**
	 * Called from workflow after the start form.
	 * Creates the role etc.
	 * Variables used from workflow are
	 * 
	 *  mwt_site:short naem/id of the site to create the role in
     *  contract_serviceName: name of the role
     *  mwt_roleType: the name of the default role, will change to default role node id
     *  mwt_startDate: start date, optional
     *  mwt_endDate: end date, optional
	 * 
	 */
	public void notify(DelegateExecution arg0) {
		
		try {

			Map<String, Object> pv = arg0.getVariables();
			/** get properties from workflow and map to Alfresco properties */
			HashMap<QName, Serializable> roleProps = new HashMap<QName, Serializable>();
			MapperUtil util = new MapperUtil();
			util.setNamespaceService(this.getServiceRegistry().getNamespaceService());
			
			/** mwt_site: site name */
			String siteName = (String) arg0.getVariableLocal("site");
			if( null == siteName) {
				throw new Exception("Cannot start process, process variable 'site' is null");
			}
	  
			/** setup the initial role properties, these will be modified as workflow progresses */
			
			/** process Id from task */
			roleProps.put(ProductionRoleModel.QN_CONTRACT_PROCESS_ID, (String) arg0.getProcessInstanceId().toString());
			
			/** nvpList_roleName: serviceName from task, added to workflow when started */
			util.taskToMap(roleProps, ProductionRoleModel.QN_ROLE_NAME, arg0);
			
			/** nvpList_roleType: roleType from task, added to workflow when started */
			util.taskToMap(roleProps, ProductionRoleModel.QN_ROLE_TYPE, arg0);
			
			/** nvpList_startDate form process, added to workflow when started */
			util.taskToMap(roleProps, ProductionRoleModel.QN_START_DATE, arg0);
			
			/** nvpList_endDate form process, added to workflow when started */
			util.taskToMap(roleProps, ProductionRoleModel.QN_END_DATE, arg0);
			
			/** nvpList_contractTemplate form process, added to workflow when started */
			util.taskToMap(roleProps, DefaultRoleModel.QN_CONTRACT_TEMPLATE, arg0);
			
			/** status when starting, should be from a const value */
			roleProps.put(ProductionRoleModel.QN_ROLE_STATUS, "Set up");
			
			/** create the new role fromm process values */
			ProductionRoleManager roleManager = new ProductionRoleManager();
		    roleManager.setServiceRegistry(this.getServiceRegistry());
		    roleProps = roleManager.createRole(roleProps, siteName);
		    
		    
		    /** set the group which will administer this process */
		    String defaultRoleManagementTeam = "Contracts admin team";
		    String groupVariable = "contract_contractApprovalRole";
		    String groupName = WorkflowUtil.getWorkflowAdminGroup(defaultRoleManagementTeam, siteName);
		    System.out.println("CreateRoleFromDefault: assigning workflow " + arg0.getProcessDefinitionId() + " / " + arg0.getProcessInstanceId() + " to group " + groupName);
		    arg0.setVariableLocal(groupVariable,groupName);
		    
		    
		    
		    util.copyToMap(roleProps, INdividu.QN_SERVICE_NAME, roleProps, ProductionRoleModel.QN_ROLE_NAME, "");
		    util.copyToMap(roleProps, INdividu.QN_SERVICE_DESCRIPTION, roleProps, ProductionRoleModel.QN_ROLE_DESCRIPTION, "");
	    	roleProps.put(INdividu.QN_SERVICE_START, roleProps.get( ProductionRoleModel.QN_START_DATE));
	    	roleProps.put(INdividu.QN_SERVICE_END, roleProps.get( ProductionRoleModel.QN_END_DATE));
	    	roleProps.put(ContractDocumentModel.QN_CONTRACT_CODE , "");
	    	roleProps.put(ContractDocumentModel.QN_CONTRACT_DATE, "");
	    	roleProps.put(ContractDocumentModel.QN_CONTRACT_APPROVAL_STATUS, ContractDocumentModel.CONTRACT_APPROVAL_NOT_APPROVED);
	    	roleProps.put(ContractDocumentModel.QN_SIGNING_TRANSACTION_ID, "");
	    	roleProps.put(ContractDocumentModel.QN_CONTRACT_APPROVAL_DATE, "");
	    	util.copyToMap(roleProps, ContractDocumentModel.QN_PAYE_STATUS, roleProps, ProductionRoleModel.QN_PAYE_STATUS, "");
	    	roleProps.put(ContractDocumentModel.QN_CONTRACT_VALUE , 0);
	    	util.copyToMap(roleProps, ContractDocumentModel.QN_RATE_PERIOD_SPECIFIER, roleProps, ProductionRoleModel.QN_RATE_PERIOD, "");
	    	util.copyToMap(roleProps, ContractDocumentModel.QN_CONTRACT_VALUE_CURRENCY, roleProps, DefaultRoleModel.QN_CURRENCY, "");
	    	util.copyToMap(roleProps, ContractDocumentModel.QN_WORKING_WEEK, roleProps, DefaultRoleModel.QN_WORKING_WEEK, "");
	    	util.copyToMap(roleProps, ContractDocumentModel.QN_CONTRACT_PAYMENT_PERIOD_SPECIFIER, roleProps, ProductionRoleModel.QN_PAYMENT_PERIOD, "");
	    	roleProps.put(ContractDocumentModel.QN_OVERTIME_PAYABLE, "false");
	    	roleProps.put(ContractDocumentModel.QN_OVERTIME_RATE, 0);
	    	roleProps.put(ContractDocumentModel.QN_CONTACT_NOTICE_PERIOD, 0);
	    	roleProps.put(ContractDocumentModel.QN_LOCATION , "");
	    	roleProps.put(ContractDocumentModel.QN_HOLIDAY_RATE, 0);
	    	
	    	roleProps.put(ContractFormsModel.QNAME_NEW_SUPPLIER_FIRST_NAME,  "" );
	    	roleProps.put(ContractFormsModel.QNAME_NEW_SUPPLIER_LAST_NAME,  "" );
	    	roleProps.put(ContractFormsModel.QNAME_NEW_SUPPLIER_EMAIL, "");
	    	
		    arg0.setVariables(util.QNameListToFlatMap(roleProps));
		       
		    
		
		} catch (Exception e) {
			
			System.out.print(e);
		}


		
	}

	
	
}
