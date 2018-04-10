package com.mwt.activiti.handlers.roleContract;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.crew.CrewService;
import com.mwt.production.ProductionManagementService;
import com.mwt.roles.ProductionRoleManager;
import com.nvp.util.NodeRefUtil;

public class WithdrawContractFromCandidate extends AbstractAlfrescoListener implements JavaDelegate {

	private static String ROLE_NODE = "nvpList_roleNodeRef";
	private static String FIRST_NAME = "contract_supplierFirstName";
	private static String LAST_NAME = "contract_supplierLastName";
	private static String SUPPLIER_EMAIL = "contract_supplierEmail";
	private static String PRODUCTION = "site";
	private static String JOB_TITLE = "contract_serviceName";
	
	@Override
	public void execute(DelegateExecution exec) throws Exception {
		
	try {
		
		    /**
		     * 
		     *  update the role status
		     *  
		     */
		    String roleUUID = NodeRefUtil.UUIDfromURL( exec.getVariable(ROLE_NODE).toString() );	
			ProductionRoleManager roleManager = new ProductionRoleManager();
			roleManager.setServiceRegistry(this.getServiceRegistry());
			roleManager.setStatusSetup(roleUUID);
			
			/**
			 * 
			 * send the candidate an email to say the offer has been withdrawn
			 * 
			 */
			try {
				
				String productionShortName = exec.getVariable(PRODUCTION).toString();
	            String productionAdminEmail = null;
		        String productionName = null;
				
		        ProductionManagementService productionService = new ProductionManagementService();
	            productionService.setServiceRegistry(this.getServiceRegistry());
	            productionAdminEmail = productionService.getAdminEmail(productionShortName);
	            productionName = productionService.getProductionName(productionShortName);
	            
	            CrewService crewService = new CrewService();
	            crewService.setServiceRegistry(this.getServiceRegistry());
	            crewService.sendOfferWithdrawEmail(exec.getVariable("contract_supplierFirstName").toString(), 
	            		                           exec.getVariable("contract_supplierLastName").toString(), 
	            		                           exec.getVariable("contract_serviceName").toString(),
	            		                           productionName,
	            		                           exec.getVariable("contract_supplierEmail").toString(), 
	            		                           productionAdminEmail);
	            	
				
			} catch (Exception e) {
			
				e.printStackTrace();
				
			}
			
			
			/**
			 * 
			 * remove the candidate personal details
			 * 
			 */
			exec.setVariable("contract_supplierFirstName", "");
			exec.setVariable("contract_supplierLastName", "");
			exec.setVariable("contract_supplierEmail", "");
			exec.setVariable("contract_dateOfBirth", "");
			exec.setVariable("contract_townOfBirth", "");
			exec.setVariable("contract_countryOfBirth", "");
			
			/**
			 * 
			 * remove the candidate contact details
			 * 
			 */
			exec.setVariable("contract_supplierMobile", "");
			exec.setVariable("contract_supplierAddress1", "");
			exec.setVariable("contract_supplierAddress2", "");
			exec.setVariable("contract_supplierAddress3", "");
			exec.setVariable("contract_supplierPostCode", "");
			
			/**
			 * 
			 * remove the candidate right to work
			 * 
			 */
			exec.setVariable("contract_rightToWorkAsserted", "");
			exec.setVariable("contract_nationalInsuranceNumber", "");
			exec.setVariable("contract_visaNumber", "");
			exec.setVariable("contract_visaExpiryDate", "");
			
			/**
			 * 
			 * remove the candidate bank details
			 * 
			 */
			exec.setVariable("prod_bankName", "");
			exec.setVariable("contract_bankAccountName", "");
			exec.setVariable("contract_bankAccountNumber", "");
			exec.setVariable("contract_bankBranchSortCode", "");
			
			
			
			
			
		} catch (Exception e) {
			
			RoleContractException rce = new RoleContractException("Error whilst updating the role status to setup, " + e.getMessage(), e);
			rce.printStackTrace();
			throw rce;
			
		}

		
	}

}
