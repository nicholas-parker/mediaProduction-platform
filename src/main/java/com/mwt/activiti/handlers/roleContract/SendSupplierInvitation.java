package com.mwt.activiti.handlers.roleContract;
  
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.alfresco.service.cmr.repository.NodeRef;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.crew.CrewService;
import com.mwt.production.ProductionManagementService;
import com.nvp.util.ProcessUtil;

public class SendSupplierInvitation extends AbstractAlfrescoListener implements JavaDelegate {

	private static String FIRST_NAME = "contract_supplierFirstName";
	private static String LAST_NAME = "contract_supplierLastName";
	private static String SUPPLIER_EMAIL = "contract_supplierEmail";
	private static String PRODUCTION = "site";
	private static String JOB_TITLE = "contract_serviceName";
	
	/**
	 * CONTEXT
	 * This is executed after the administration team has setup the contract information.
	 * 
	 * FUNCTION
	 * Checks to see if a member with the suppliers email exists, creates an account if not exists
	 * Sends a job invitation by email to the supplier
	 * 
	 */
	@Override
	public void execute(DelegateExecution exec) throws Exception {
		
		System.out.println("Preparing to send invitation to candidate");
		
		/**
		 * 
		 * check we have valid firstName, lastName,supplier email, parameters in the workflow
		 * 
		 */
	     if( !hasValue(FIRST_NAME, exec)) {
	    
	    	 System.out.println("Unable to send invitation to candidate as no first name provided");
	    	 throw new Exception("Unable to send invitation to candidate as no first name provided");
	    	 
	     }
	     String firstName = exec.getVariable(FIRST_NAME).toString();
	     
         if( !hasValue(LAST_NAME, exec)) {
	    
        	 System.out.println("Unable to send invitation to candidate as no last name provided");
	    	 throw new Exception("Unable to send invitation to candidate as no last name provided");
	    	 
	     }
         String lastName = exec.getVariable(LAST_NAME).toString();
         
         if( !hasValue(SUPPLIER_EMAIL, exec)) {
	    
        	 System.out.println("Unable to send invitation to candidate as no first name provided");
	    	 throw new Exception("Unable to send invitation to candidate as no first name provided");
	    	 
	     }
         String email = exec.getVariable(SUPPLIER_EMAIL).toString();
         
         if( !hasValue(PRODUCTION, exec)) {
     	    
        	 System.out.println("Unable to send invitation to candidate as no production provided");
	    	 throw new Exception("Unable to send invitation to candidate as no production provided");
	    	 
	     }
         String productionShortName = exec.getVariable(PRODUCTION).toString();
         
         String productionAdminEmail = null;
         String productionName = null;
         try {
        
        	 ProductionManagementService productionService = new ProductionManagementService();
             productionService.setServiceRegistry(this.getServiceRegistry());
             productionAdminEmail = productionService.getAdminEmail(productionShortName);
             productionName = productionService.getProductionName(productionShortName);
             
         } catch (Exception e) {
        	 
        	 System.out.println("Unable to send invitation. Exception when finiding production name for site name");
        	 e.printStackTrace();
	    	 
         }

         if( !hasValue(JOB_TITLE, exec)) {
      	    
        	 System.out.println("Unable to send invitation to candidate as no job title provided");
	    	 throw new Exception("Unable to send invitation to candidate as no job title provided");
	    	 
	     }
         String jobTitle = exec.getVariable(JOB_TITLE).toString();

         System.out.println("Everyting in order, sending invitation to candidate");
         
        /**
         * 
         * everything on order now send the invitation
         * 
         */
        try {
        
            CrewService crewService = new CrewService();
            crewService.setServiceRegistry(this.getServiceRegistry());
            
            Set<NodeRef> members = crewService.getGlobalMembersByEmail( exec.getVariable(SUPPLIER_EMAIL).toString());
            if(null == members || members.size() == 0) {
         
            	/**
        		 * 
        		 * create a new member if we dont have the email address already
        		 * 
        		 */
                System.out.println("Creating a new account for member");
            	Map<String, String> crew =crewService.createNewStandbyMember(firstName, lastName, email);
            	
            	/**
            	 * 
            	 * send the email notification to the new user
            	 * 
            	 */
            	System.out.println("Sending invitation to new member");
            	crewService.sendJobNotificationNewSupplier(email, crew.get("password"), firstName, lastName, jobTitle, productionName, email, productionAdminEmail);
            	
            	/**
            	 * 
            	 * give new user write access to process package so they can upload right to work documents
            	 * 
            	 */
            	ProcessUtil.giveUserWritePackageAccess(this.getServiceRegistry(), email, exec);
            
            } else {
            	
               /**
                * 
                * send an invitation to an existing member
                * 
                */
            	System.out.println("Sending invitation to existing member");
            }

            /**
             * 
             * set the suppliers username, their email, as a process variable
             * this is used to assign the candidate review task to the right member
             * 
             */
            exec.setVariable(this.getMapper().qNameToFlat(ContractDocumentModel.QN_SUPPLIER), email);
        
        } catch (Exception e) {

        	e.printStackTrace();
        	
        }
        
        
	}

}
