package com.mwt.roles;
 
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.json.simple.JSONObject;

import com.mwt.contract.ContractService;
import com.mwt.contract.model.ContractCrewEngagementModel;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.roles.DefaultRoleModel;
import com.nvp.alfresco.datalist.DataListUtil;
import com.nvp.util.DateUtil;
import com.nvp.util.MapperUtil;
import com.nvp.util.NodeRefUtil;
import com.nvp.util.PaymentValueUtil;

public class ProductionRoleManager {

	public static String DATALIST_CONTAINER_ID = "dataLists";
	public static String PRODUCTION_ROLE_CONTAINTER_ID = "productionRoleContainer";
	
	private ServiceRegistry serviceRegistry;
	
    public void setServiceRegistry(ServiceRegistry registry) {
        this.serviceRegistry = registry;
    }
    
    /**
     * Creates a new role for a given site.  Role is created under 
     * 'Production Role' node.
     * 
     * @param RoleForm a JSON representation of the data provided by the submitted form
     * @param siteName the name of the site to make the role in
     * @throws ProductionRoleException
     */
    public HashMap<QName, Serializable> createRole(HashMap<QName, Serializable> roleProperties, String siteName) throws ProductionRoleException {
    	
    	/** things we need later */
    	HashMap<QName, Serializable> serviceProperties = new HashMap<QName, Serializable>();
    	HashMap<QName, Serializable> contractProperties = new HashMap<QName, Serializable>();
    	NodeService nodeService = serviceRegistry.getNodeService();
    	ContractService contractService = new ContractService();
    	contractService.setServiceRegistry(serviceRegistry);
    	MapperUtil util = new MapperUtil(serviceRegistry.getNamespaceService());
    	
    	/** get the contained where the roles for this site live + the site info */
    	SiteService siteService = this.serviceRegistry.getSiteService();
    	NodeRef roleContainer = getProductionRoleContainer(siteName);
    	// SiteInfo siteInfo = siteService.getSite(roleContainer);

    	/** get the default properties from the default role. Use either the default role UUID or default role name */
		Map<QName, Serializable> defaultRoleProps = null;
    	String nodeUUID = null; // = (String) RoleForm.get("prop_dl_defaultRole");
    	String defaultRoleNodeRef = null;
    	if(nodeUUID != null) {
    		
    		/** have a default role node id, try this to get the default role properties */
    		StringBuilder sb = new StringBuilder();
    		sb.append("workspace://SpacesStore/");
    		sb.append( (String) nodeUUID );
    		defaultRoleNodeRef = sb.toString();
    		String roleType = (String) roleProperties.get(ProductionRoleModel.QN_ROLE_TYPE);
    		defaultRoleProps = getDefaultRoleProperties(defaultRoleNodeRef.toString());

    	} else {
    		
    		/** no default role id property in form, try to get properties using role role name */
    		String key = "nvpList_roleName";
    		String defaultRoleName = (String) roleProperties.get(ProductionRoleModel.QN_ROLE_TYPE);
    		if(defaultRoleName != null) {
    			defaultRoleProps = getDefaultRolePropertiesForName(siteName, defaultRoleName); 
    			StringBuilder sb = new StringBuilder();
        		sb.append("workspace://SpacesStore/");
        		sb.append( (String) defaultRoleProps.get(ContentModel.PROP_NODE_UUID) );
        		defaultRoleNodeRef = sb.toString();
    			;
    		}
    		 
    	}
    	
		/** add the default role properties into the existing properties map for new the new role node */
    	if(defaultRoleProps != null) {
    		roleProperties.put(ProductionRoleModel.QN_ROLE_DESCRIPTION, defaultRoleProps.get(DefaultRoleModel.QN_TYPE_DESCRIPTION));
    		roleProperties.put(ProductionRoleModel.QN_CHARGE_CODE, defaultRoleProps.get(DefaultRoleModel.QN_CHARGE_CODE));
    		roleProperties.put(ProductionRoleModel.QN_PAYE_STATUS, defaultRoleProps.get(DefaultRoleModel.QN_PAYE_STATUS));
    		roleProperties.put(ProductionRoleModel.QN_MIN_BUDGET, defaultRoleProps.get(DefaultRoleModel.QN_BUDGET_MIN));
    		roleProperties.put(ProductionRoleModel.QN_MAX_BUDGET, defaultRoleProps.get(DefaultRoleModel.QN_BUDGET_MAX));
    		roleProperties.put(ProductionRoleModel.QN_CURRENCY, defaultRoleProps.get(DefaultRoleModel.QN_CURRENCY));
    		roleProperties.put(ProductionRoleModel.QN_RATE_PERIOD, defaultRoleProps.get(DefaultRoleModel.QN_RATE_PERIOD));
    		roleProperties.put(ProductionRoleModel.QN_PAYMENT_PERIOD, defaultRoleProps.get(DefaultRoleModel.QN_PAYMENT_PERIOD));
    		roleProperties.put(ProductionRoleModel.QN_ROLE_CATEGORY, defaultRoleProps.get(DefaultRoleModel.QN_TYPE_CATEGORY));
    		roleProperties.put(ContractDocumentModel.QN_CONTRACT_DELIVERABLE_TYPE, defaultRoleProps.get(DefaultRoleModel.QN_DELIVERABLE_TYPE));
    		roleProperties.put(ContractCrewEngagementModel.QN_CONTACT_NOTICE_PERIOD , defaultRoleProps.get(DefaultRoleModel.QN_NOTICE_PERIOD));
    		roleProperties.put(ContractCrewEngagementModel.QN_HOLIDAY_RATE , defaultRoleProps.get(DefaultRoleModel.QN_DAYS_PAID_HOLIDAY));
    		roleProperties.put(ContractCrewEngagementModel.QN_OVERTIME_PAYABLE , defaultRoleProps.get(DefaultRoleModel.QN_OVERTIME_PAYABLE));
    	}
    	
    	/** calculate the totalBudgetMin/Max */
    	try {
    		
    		Date startDate = DateUtil.toDate(roleProperties.get(ProductionRoleModel.QN_START_DATE));
    		Date endDate = DateUtil.toDate( roleProperties.get(ProductionRoleModel.QN_END_DATE));
    		
    		roleProperties.putAll( calcMinMaxBudget(startDate,
    				                                endDate,
    				                                (int) roleProperties.get(ProductionRoleModel.QN_MIN_BUDGET),
    				                                (int) roleProperties.get(ProductionRoleModel.QN_MAX_BUDGET),
    				                                roleProperties.get(ProductionRoleModel.QN_RATE_PERIOD).toString()));
    		
    	} catch (Exception e) {
    		
    		System.out.println("ERROR: Unable to calculate the min/max total budget when creating a production role. " + e.getMessage());
    	}
		
    	/** create the new node which is the production role */
  		String nodeName = (String) roleProperties.get(ProductionRoleModel.QN_ROLE_NAME);
  	  	NodeRef roleNode = nodeService.createNode(
  	                        roleContainer, 
  	                        ContentModel.ASSOC_CONTAINS, 
  	                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
                                  QName.createValidLocalName(nodeName)),
  	                        ProductionRoleModel.QN_PRODUCTION_ROLE, 
  	                        roleProperties).getChildRef();
  	  	
  	  	roleProperties.put(ProductionRoleModel.QN_ROLE_NODEREF, roleNode.toString());
  	  	
  	  	/** new role done......add some additional default role properties to the properties list as these will be used for the process variables  */
  	    util.copyToMap(roleProperties, DefaultRoleModel.QN_CURRENCY, defaultRoleProps, DefaultRoleModel.QN_CURRENCY, "");
  	    util.copyToMap(roleProperties, DefaultRoleModel.QN_WORKING_WEEK, defaultRoleProps, DefaultRoleModel.QN_WORKING_WEEK, "");
  	    util.copyToMap(roleProperties, DefaultRoleModel.QN_DELIVERABLE_TYPE, defaultRoleProps, DefaultRoleModel.QN_DELIVERABLE_TYPE, "");
  	    util.copyToMap(roleProperties, DefaultRoleModel.QN_PAYMENT_PERIOD, defaultRoleProps, DefaultRoleModel.QN_PAYMENT_PERIOD, "");
  	    
  	      	  	
  	  	return roleProperties;
  	  	
    }
    
    public void setRoleBudget() {
    	
    }
    
//    public void createContractTemplate() {
//    
//  	  	/**
//  	  	 * If we have a contract template for this role and copy to a new node
//  	  	 * which will be the actual contract document for this node.
//  	  	 * 
//  	  	 * Associate the new contract document with the role node and 
//  	  	 * associate the contract document with the role
//  	  	 * 
//  	  	 * Set the contract status to NOT_APPROVED
//  	  	 */
//  	    NodeRef contractDocument = null;
//  	    String contractFileName = getRoleContractFilename(roleProperties);
//  	  	NodeRef templateDocument = getDefaultRoleTemplateDocument(defaultRoleNodeRef.toString());
//
//  		// contract properties created here
//		contractProperties.put(ContractDocumentModel.QN_CONTRACT_CODE, "CONTRACT_CODE");	  		
//  		contractProperties.put(ContractDocumentModel.QN_PRODUCTION_ROLE, roleNode);
//  		// properties copied from the role
//  		contractProperties.put(ContractDocumentModel.QN_CONTRACT_VALUE_CURRENCY, (String) roleProperties.get(ProductionRoleModel.QN_CURRENCY));
//  		contractProperties.put(ContractDocumentModel.QN_PAYE_STATUS, defaultRoleProps.get(DefaultRoleModel.QN_PAYE_STATUS));
//		contractProperties.put(ContractDocumentModel.QN_CONTRACT_DELIVERABLE_TYPE, defaultRoleProps.get(DefaultRoleModel.QN_DELIVERABLE_TYPE));
//		contractProperties.put(ContractDocumentModel.QN_CONTRACT_APPROVAL_STATUS, ContractDocumentModel.CONTRACT_APPROVAL_NOT_APPROVED);
//        // TO DO -- put the notice period in the default role
//		contractProperties.put(ContractDocumentModel.QN_CONTACT_NOTICE_PERIOD, "14");
//        // TO DO -- put overtime available & rate in the default role
//		contractProperties.put(ContractDocumentModel.QN_OVERTIME_PAYABLE, "NO");
//		contractProperties.put(ContractDocumentModel.QN_OVERTIME_RATE, 0);
//		contractProperties.put(ContractDocumentModel.QN_CONTRACT_PAYMENT_PERIOD_SPECIFIER, defaultRoleProps.get(DefaultRoleModel.QN_PAYMENT_PERIOD));
//		contractProperties.put(ContractDocumentModel.QN_RATE_PERIOD_SPECIFIER, defaultRoleProps.get(DefaultRoleModel.QN_RATE_PERIOD));
//		contractProperties.put(ContractDocumentModel.QN_WORKING_WEEK, defaultRoleProps.get(DefaultRoleModel.QN_WORKING_WEEK));
//		// TO DO -- put holiday rate in the default role
//		contractProperties.put(ContractDocumentModel.QN_HOLIDAY_RATE, 0);
//		serviceProperties.put(ServicePeriodModel.QN_SERVICE_NAME, roleProperties.get(ProductionRoleModel.QN_ROLE_NAME));
//		serviceProperties.put(ServicePeriodModel.QN_SERVICE_START, roleProperties.get(ProductionRoleModel.QN_START_DATE));
//		serviceProperties.put(ServicePeriodModel.QN_SERVICE_END, roleProperties.get(ProductionRoleModel.QN_END_DATE));
//		serviceProperties.put(ServicePeriodModel.QN_SERVICE_DESCRIPTION, defaultRoleProps.get(DefaultRoleModel.QN_TYPE_DESCRIPTION));
//
//		
//		if( templateDocument != null) {
//    		try {
//
//    			/**
//    			 * create the document
//    			 */
//    			contractDocument = contractService.createServiceContractForRole(contractProperties,
//    					                                                        serviceProperties,
//    					                                                        contractFileName, 
//    					                                                        templateDocument);
//    		
//    			/**
//    			 * create the associations
//    			 */
//    			nodeService.createAssociation(roleNode,
//    				                     	  contractDocument,
//    				                     	  ProductionRoleModel.QN_CONTRACT_DOCUMENTS);
//    			
//    			nodeService.createAssociation(contractDocument,
//    					                      roleNode,
//    					                      ContractDocumentModel.QN_PRODUCTION_ROLE);
//    		
//    		} catch (Exception e) {
//    			throw new ProductionRoleException("Error creating service contract for role", e);
//    		}
//    		
//    		
//  	  	}
//  	  		
//    }
    
    
//        /**
//         * 
//         * create the workflow to manage the contract.
//         * This is not used if we are starting using the Angular UI because workflow is started up front
//         * @return 
//         * 
//         */
//		public void createWorkflowForContractManagement(String siteName, HashMap<QName, Serializable> defaultRoleProps) {
//			
//		
//  	  	String approvalGroup = getWorkflowAdminGroup((String) defaultRoleProps.get(DefaultRoleModel.QN_ADMINISTRATION_TEAM), siteName);
//  	  	String managementProcess = getWorkflowProcess((String) defaultRoleProps.get(DefaultRoleModel.QN_PROCESS_NAME), siteName);
//  	  	
//  	    if(managementProcess != null) {
//  	    	
//  	    	// due date for first task
//  	    	Date dueDate = new Date();
//  	    	
//  	    	WorkflowService workflowService = this.serviceRegistry.getWorkflowService();
//  	  		NodeRef workflowPackage = workflowService.createPackage(null);
//  	  		if(contractDocument != null) {
//  	  			nodeService.addChild(workflowPackage, contractDocument, ContentModel.ASSOC_CONTAINS,
//  	  				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
//  	  				QName.createValidLocalName(
//  	  				(String)nodeService.getProperty(contractDocument, ContentModel.PROP_NAME))));
//  	  		}
//  	  		Map<QName, Serializable> workflowProps = new HashMap<QName, Serializable>();
//  	  		
//  	  		workflowProps.put(WorkflowModel.ASSOC_PACKAGE, workflowPackage);
//  	  		workflowProps.put(ContractDocumentModel.QN_CONTRACT_APPROVAL_ROLE, approvalGroup);
//  	  		workflowProps.put(QName.createQName(SiteModel.SITE_MODEL_URL, "siteName"), siteName );
//  	  		workflowProps.put(WorkflowModel.PROP_DESCRIPTION, "Enter the contract information for " + serviceProperties.get(ServicePeriodModel.QN_SERVICE_NAME));
//  	  		workflowProps.put(DefaultRoleModel.QN_BUDGET_MIN, defaultRoleProps.get(DefaultRoleModel.QN_BUDGET_MIN) );
//  	  	    workflowProps.put(DefaultRoleModel.QN_BUDGET_MAX, defaultRoleProps.get(DefaultRoleModel.QN_BUDGET_MAX) );
//  	  		workflowProps.putAll(contractProperties);
//  	  		workflowProps.putAll(serviceProperties);
//  	  		
//  	  		try {
//  	  			 
//  	  			WorkflowDefinition workflowDefinition = workflowService.getDefinitionByName(managementProcess);
//  	  			WorkflowPath wfPath = workflowService.startWorkflow(workflowDefinition.getId(), workflowProps);
//  	  			String wfId = wfPath.getId();
//  	  			nodeService.setProperty(roleNode, ProductionRoleModel.QN_CONTRACT_PROCESS_ID, wfId);
//  	  			System.out.println("Started workflow [" + wfId + "]");
//  	  			MapperUtil.printQNameVars(workflowProps);
//  	  			
//  	  		} catch(Exception e) {
//  	  			e.printStackTrace();
//  	  			throw new ProductionRoleException(e);
//  	  		}
//  	  	}
//	}
	
	
	public void getRole(){
		
	}
	
	public void updateRole() {
		
	}
	
	public void deleteRole() {
		
	}
	
	public Map<QName, Serializable> calcMinMaxBudget(Date startDate, Date endDate, int budgetMin, int budgetMax, String paymentPeriod) throws Exception {

		if(null == startDate) {
			throw new Exception("Unable to setTotalBudgetMinMax on role becuase startDate was null");
		}
		
		if(null == endDate) {
			throw new Exception("Unable to setTotalBudgetMinMax on role becuase endDate was null");
		}
		
		if(startDate.after(endDate)) {
			throw new Exception("Unable to setTotalBudgetMinMax on role becuase startDate was after endDate");
		}
		
		if(budgetMin < 0) {
			throw new Exception("Unable to setTotalBudgetMinMax on role becuase budgetMin < 0");
		}
		
		if(budgetMax < 0) {
			throw new Exception("Unable to setTotalBudgetMinMax on role becuase budgetMax < 0");
		}	

		if(null == paymentPeriod || paymentPeriod.isEmpty()) {
			throw new Exception("Unable to setTotalBudgetMinMax on role because the paymentPeriod is empty or null");
		}
		
		float totalBudgetMin = 0;
		float totalBudgetMax = 0;
		
		/**
		 * 
		 * calculate the min, max total budget based on payment period
		 * 
		 */
		if(paymentPeriod.equals(ContractCrewEngagementModel.RATE_PERIOD_PER_HOUR)) {
			
			/** TODO - provide this calculation */
			
		}
		
        if(paymentPeriod.equals(ContractCrewEngagementModel.RATE_PERIOD_PER_DAY)) {
			
			totalBudgetMin = PaymentValueUtil.getTotalPaymentDayRate(startDate, endDate, budgetMin, 5);
			totalBudgetMax = PaymentValueUtil.getTotalPaymentDayRate(startDate, endDate, budgetMax, 5);
			
		}
        
        if(paymentPeriod.equals(ContractCrewEngagementModel.RATE_PERIOD_PER_WEEK)) {
			
        	/** TODO - provide this calculation */
        	
		}
        
        if(paymentPeriod.equals(ContractCrewEngagementModel.RATE_PERIOD_PER_MONTH)) {
			
			/** TODO - provide this calculation */
			
		}
        
        HashMap<QName, Serializable> totalBudgetMinMax = new HashMap<QName, Serializable>();
        totalBudgetMinMax.put(ProductionRoleModel.QN_TOTAL_BUDGET_MIN, totalBudgetMin);
        totalBudgetMinMax.put(ProductionRoleModel.QN_TOTAL_BUDGET_MAX, totalBudgetMax);
        return totalBudgetMinMax;

	}
	
	public void setTotalBudgetMinMaxNodeURL(String roleNodeURL, Date startDate, Date endDate, int budgetMin, int budgetMax, String paymentPeriod) throws Exception {
		
		this.setTotalBudgetMinMax(NodeRefUtil.UUIDfromURL(roleNodeURL), startDate, endDate, budgetMin, budgetMax, paymentPeriod);
		
	}
	/**
	 * 
	 * Sets the totalBUdgetMin and totalBugetMax on a node
	 * @param roleNodeUUID
	 * @param startDate
	 * @param endDate
	 * @param budgetMin
	 * @param budgetMax
	 * @param paymentPeriod
	 * @throws Exception
	 */
	public void setTotalBudgetMinMax(String roleNodeUUID, Date startDate, Date endDate, int budgetMin, int budgetMax, String paymentPeriod) throws Exception {
		
		if(null == roleNodeUUID || roleNodeUUID.isEmpty()) {
			throw new Exception("Unable to setTotalBudgetMinMax on role becuase roleNodeUUID was empty or null");
		}
		
		if(null == startDate) {
			throw new Exception("Unable to setTotalBudgetMinMax on role becuase startDate was null");
		}
		
		if(null == endDate) {
			throw new Exception("Unable to setTotalBudgetMinMax on role becuase endDate was null");
		}
		
		if(startDate.after(endDate)) {
			throw new Exception("Unable to setTotalBudgetMinMax on role becuase startDate was after endDate");
		}
		
		if(budgetMin < 0) {
			throw new Exception("Unable to setTotalBudgetMinMax on role becuase budgetMin < 0");
		}
		
		if(budgetMax < 0) {
			throw new Exception("Unable to setTotalBudgetMinMax on role becuase budgetMax < 0");
		}	

		if(null == paymentPeriod || paymentPeriod.isEmpty()) {
			throw new Exception("Unable to setTotalBudgetMinMax on role because the paymentPeriod is empty or null");
		}
		
		float totalBudgetMin = 0;
		float totalBudgetMax = 0;
		
		/**
		 * 
		 * calculate the min, max total budget based on payment period
		 * 
		 */
		if(paymentPeriod.equals(ContractCrewEngagementModel.RATE_PERIOD_PER_HOUR)) {
			
			/** TODO - provide this calculation */
			
		}
		
        if(paymentPeriod.equals(ContractCrewEngagementModel.RATE_PERIOD_PER_DAY)) {
			
        	totalBudgetMin = PaymentValueUtil.getTotalPaymentDayRate(startDate, endDate, budgetMin, 5);
			totalBudgetMax = PaymentValueUtil.getTotalPaymentDayRate(startDate, endDate, budgetMax, 5);
			
		}
        
        if(paymentPeriod.equals(ContractCrewEngagementModel.RATE_PERIOD_PER_WEEK)) {
			
        	/** TODO - provide this calculation */
        	
		}
        
        if(paymentPeriod.equals(ContractCrewEngagementModel.RATE_PERIOD_PER_MONTH)) {
			
			/** TODO - provide this calculation */
			
		}
        
        this.setRoleProperty(roleNodeUUID, ProductionRoleModel.QN_TOTAL_BUDGET_MIN, totalBudgetMin);
        this.setRoleProperty(roleNodeUUID, ProductionRoleModel.QN_TOTAL_BUDGET_MAX, totalBudgetMax);
        
	}

	/**
	 * 
	 * Sets the status of the role to indicate this role is in setup status.
	 * Setup is when the role is being prepared for the first time or it has been rejected by a supplier
	 * and a new suppler is required.
	 * 
	 * @param nodeUUID
	 * @throws ProductionRoleException
	 */
	public void setStatusSetup(String nodeUUID) throws ProductionRoleException {
		
		this.setRoleProperty(nodeUUID, ProductionRoleModel.QN_ROLE_STATUS, ProductionRoleModel.ROLE_STATUS_SETUP);
	}

	/**
	 * 
	 * Sets the status of the role to indicate with the client for review
	 * 
	 * @param nodeUUID
	 * @throws ProductionRoleException
	 */
	public void setStatusSupplierReview(String nodeUUID) throws ProductionRoleException {
		
		this.setRoleProperty(nodeUUID, ProductionRoleModel.QN_ROLE_STATUS, ProductionRoleModel.ROLE_STATUS_SUPPLIER_REVIEW);
		
	}
	
	/**
	 * 
	 * Sets the status of the role to indicate that the supplier/candidate has accepted this role
	 * This function is executed as admin because status is accepted
	 * 
	 * @param nodeUUID
	 * @throws ProductionRoleException
	 */
	public void setStatusAccepted(final String nodeUUID) throws ProductionRoleException {
		
		final ServiceRegistry registry = this.serviceRegistry;
		
		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            public Object doWork() throws Exception {

               ProductionRoleManager.setRoleProperty(registry, nodeUUID, ProductionRoleModel.QN_ROLE_STATUS, ProductionRoleModel.ROLE_STATUS_ACCEPTED); 	   
         	   return null;
         	   
            }
        });
		
	}
	
	/**
	 * 
	 * Sets the status of the role to indicate that the supplier/candidate has rejected this role
	 * This function is executed as admin because status is accepted
	 * 
	 * @param nodeUUID
	 * @throws ProductionRoleException
	 */
	public void setStatusDeclined(final String nodeUUID) throws ProductionRoleException {
		
		final ServiceRegistry registry = this.serviceRegistry;
		
		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            public Object doWork() throws Exception {

               ProductionRoleManager.setRoleProperty(registry, nodeUUID, ProductionRoleModel.QN_ROLE_STATUS, ProductionRoleModel.ROLE_STATUS_DECLINED); 	   
         	   return null;
         	   
            }
        });
		
	}


	/**
	 * 
	 * Sets the status to indicate that role has been approved
	 * 
	 * @param nodeUUID
	 * @throws ProductionRoleException
	 */
    public void setStatusApproved(String nodeUUID) throws ProductionRoleException {
		
		this.setRoleProperty(nodeUUID, ProductionRoleModel.QN_ROLE_STATUS, ProductionRoleModel.ROLE_STATUS_APPROVED);
		
	}
    
    /**
	 * 
	 * Sets the status to indicate that supplier/candidate was rejected and a new supplier/candidate is required
	 * 
	 * @param nodeUUID
	 * @throws ProductionRoleException
	 */
    public void setStatusNewSupplier(String nodeUUID) throws ProductionRoleException {
		
		this.setRoleProperty(nodeUUID, ProductionRoleModel.QN_ROLE_STATUS, ProductionRoleModel.ROLE_STATUS_NEW_SUPPLIER);
		
	}
    
    /**
     * 
     * Sets the status to indicate that this role has been withdrawn
     * End state.
     * 
     * @param nodeUUID
     * @throws ProductionRoleException
     */
    public void setStatusCanceled(String nodeUUID) throws ProductionRoleException {
		
		this.setRoleProperty(nodeUUID, ProductionRoleModel.QN_ROLE_STATUS, ProductionRoleModel.ROLE_STATUS_CANCELED);
		
	}
	
    /**
     * 
     * Sets the status to indicate that this role is currently active on the production,
     * e.g. the current date is between the start and end date of the role
     * 
     * @param nodeUUID
     * @throws ProductionRoleException
     */
    public void setStatusActive(String nodeUUID) throws ProductionRoleException {
		
		this.setRoleProperty(nodeUUID, ProductionRoleModel.QN_ROLE_STATUS, ProductionRoleModel.ROLE_STATUS_ACTIVE);
		
	}

    /**
     * 
     * Sets the status to indicate that this role has been completed,
     * e.g. the current date is after the start and end date of the role
     * End state
     * 
     * @param nodeUUID
     * @throws ProductionRoleException
     */
    public void setStatusCompleted(String nodeUUID) throws ProductionRoleException {
		
		this.setRoleProperty(nodeUUID, ProductionRoleModel.QN_ROLE_STATUS, ProductionRoleModel.ROLE_STATUS_COMPLETED);
		
	}

	public void setTotalContractsValue(String nodeUUID, Float value)  throws ProductionRoleException {
		
		this.setRoleProperty(nodeUUID, ProductionRoleModel.QN_TOTAL_CONTRACTS_AMOUNT, value);
		
	}
	
	public void calcTotaclContractsValue(String nodeUUID) throws ProductionRoleException {
		
	}
	
	public void setRoleProperty(String nodeUUID, QName param, Serializable value) throws ProductionRoleException {
		
		try {
			
			NodeRef roleNode = NodeRefUtil.NodeReffromUUID(nodeUUID);
			Map<QName, Serializable> newProperty = new HashMap<QName, Serializable>(1);
			newProperty.put(param, value);
			NodeRefUtil.mergeNodeProperties(roleNode, newProperty, this.serviceRegistry.getNodeService());
			
		} catch (Exception e) {
			
			throw new ProductionRoleException(e);
		}
	}
	
    public static void setRoleProperty(ServiceRegistry registry, String nodeUUID, QName param, Serializable value) throws ProductionRoleException {
		
		try {
			
			NodeRef roleNode = NodeRefUtil.NodeReffromUUID(nodeUUID);
			Map<QName, Serializable> newProperty = new HashMap<QName, Serializable>(1);
			newProperty.put(param, value);
			NodeRefUtil.mergeNodeProperties(roleNode, newProperty, registry.getNodeService());
			
		} catch (Exception e) {
			
			throw new ProductionRoleException(e);
		}
	}
    public void setRoleProperties(NodeRef nodeRef, Map<QName, Serializable> properties) throws ProductionRoleException {
		
		try {
			
			NodeRefUtil.mergeNodeProperties(nodeRef, properties, this.serviceRegistry.getNodeService());
			
		} catch (Exception e) {
			
			throw new ProductionRoleException(e);
		}
	}
	
	
	/**
	 * Returns the NodeRef for the container which holds
	 * Production Role nodes.
	 * 
	 * @param siteName
	 * @return NodeRef the Production Role container
	 */
	public NodeRef getProductionRoleContainer(String siteName) {
		
		/**
		 * Get site dataListContainer, search through children till found 
		 * the child with the name PRODUCTION_ROLE_LIST_NAME.  This is the
		 * Production Role container
		 */
		SiteService siteService = this.serviceRegistry.getSiteService();
        NodeRef dataListContainer = siteService.getContainer(siteName, DATALIST_CONTAINER_ID);
        NodeService nodes = this.serviceRegistry.getNodeService();
        
        List<ChildAssociationRef> children = nodes.getChildAssocs(dataListContainer);
        for(ChildAssociationRef child : children){
        
        	Map<QName, Serializable> nodeListProps = nodes.getProperties(child.getChildRef());
            if(nodeListProps.containsKey(ContentModel.PROP_NAME)) {
            	String cm_title = (String) (nodeListProps.get(ContentModel.PROP_NAME));
            	if( cm_title.contentEquals(ProductionRoleModel.PRODUCTION_ROLE_LIST_NAME) ) {
            		return child.getChildRef(); }
            }
        }
        
        /**
         * Production Role container not found, create it now
         */
        return null;
	}
	
	public NodeRef createProductionRoleContainer(String siteName) {
		
		SiteService siteService = this.serviceRegistry.getSiteService();
        NodeRef dataListContainer = siteService.getContainer(siteName, DATALIST_CONTAINER_ID);
        
		return null;
	}
	
	private Map<QName, Serializable> JsonToPropMap(JSONObject JsonRoleForm) throws ProductionRoleException {
		
				
		HashMap<QName, Serializable> roleProps = new HashMap<QName, Serializable>();
		
        String roleType = (String) JsonRoleForm.get(ProductionRoleModel.FORM_PROP_ROLE_TYPE);
        if(null == roleType || roleType.isEmpty()) {
        	roleType = ProductionRoleModel.VALUE_DEFAULT_ROLE_TYPE;
        }
		roleProps.put(ProductionRoleModel.QN_ROLE_TYPE, roleType);
        
        String roleName = (String) JsonRoleForm.get(ProductionRoleModel.FORM_PROP_ROLE_NAME);
        if(null == roleName || roleName.isEmpty()) {
        	throw new ProductionRoleException("Unable to create role, name is null or empty");
        }
        roleProps.put(ProductionRoleModel.QN_ROLE_NAME, roleName);
        
        
        roleProps.put(ProductionRoleModel.QN_START_DATE, 
                      (String) JsonRoleForm.get(ProductionRoleModel.FORM_PROP_START_DATE));
                
        roleProps.put(ProductionRoleModel.QN_END_DATE, 
                (String) JsonRoleForm.get(ProductionRoleModel.FORM_PROP_END_DATE));
  
        
        return roleProps;
	}
	
	/**
	 * Get the properties of the default role
	 * 
	 * @param nodeUUID
	 * @return
	 */
	private Map<QName, Serializable> getDefaultRoleProperties(String nodeUUID) {
		
		NodeRef defaultRoleNodeRef = new NodeRef(nodeUUID);
		NodeService nodeService = this.serviceRegistry.getNodeService();
		return nodeService.getProperties(defaultRoleNodeRef);
	
	}

	/**
	 * Given the name of a defaultRole and the site returns the properties for that role.
	 * Because it matches on name returns the first one found
	 * 
	 * @param siteName
	 * @param defaultRoleName
	 * @return properties for defaultRole
	 * 
	 */
	private Map<QName, Serializable> getDefaultRolePropertiesForName(String siteName, String defaultRoleName) {
	
		DataListUtil util = new DataListUtil(this.serviceRegistry.getSiteService(), this.serviceRegistry.getNodeService());
		NodeRef defaultRoleFolder = util.getDataListFolder(siteName, "defaultRoles");
		
		NodeService nodeService = this.serviceRegistry.getNodeService();
		List<ChildAssociationRef> defaultRoleRefs = nodeService.getChildAssocs(defaultRoleFolder);
        for(ChildAssociationRef child : defaultRoleRefs){
        
        	Map<QName, Serializable> nodeListProps = nodeService.getProperties(child.getChildRef());
            if(nodeListProps.containsKey(DefaultRoleModel.QN_TYPE_NAME)) {
            	String roleType = (String) (nodeListProps.get(DefaultRoleModel.QN_TYPE_NAME));
            	if( roleType.contentEquals(defaultRoleName) ) {
            		return nodeListProps; }
            }
        }
        
        /** not found */
        return null;
	}
	
	/**
	 * returns the node ref of the default role QN_CONTRACT_TEMPLATE
	 * 
	 * @param nodeUUID
	 * @return
	 */
	private NodeRef getDefaultRoleTemplateDocument(String nodeUUID) {
		
		NodeRef defaultRoleNodeRef = new NodeRef(nodeUUID);
		NodeService nodeService = this.serviceRegistry.getNodeService();
		
		List<AssociationRef> assocs = nodeService.getTargetAssocs(defaultRoleNodeRef, RegexQNamePattern.MATCH_ALL);
		for(AssociationRef assoc : assocs) {
			if(assoc.getTypeQName().isMatch(DefaultRoleModel.QN_CONTRACT_TEMPLATE)){
				return assoc.getTargetRef();
			}
		}
		return null;
	}
	/**
	 * Makes a copy of the contract template in the Contracts folder and names
	 * it with the role name.
	 * Sets up the meta-data and aspects on the contract.
	 * Associates the new contract document with the role.
	 * 
	 * @param contractTemplate the nodeRef of the document which is the template contract for this type of role
	 * @param role the nodeRef for the role itself
	 */
	private String getRoleContractFilename(Map<QName, Serializable> roleProps) {
		
		StringBuilder targetFileName = new StringBuilder();
		targetFileName.append(roleProps.get(ProductionRoleModel.QN_ROLE_NAME));
		targetFileName.append(".docx");
		return targetFileName.toString();

		
	}
	
	
	
	/**
	 * For a given site and a given logical administration process returns the
	 * Activiti process definitiion
	 * 
	 * @param processName
	 * @param siteName
	 * @return
	 */
	private String getWorkflowProcess(String processName, String siteName){
		if(processName.contentEquals("Contract management process")){
			return "activiti$role-offer-contract_v0-1";
		} else {
			return null;
		}
	}
	
	/**
	 *  Given a site and defaultRoleName returns the number of existing roles which are
	 *  based on the given default role. 
	 */
	private int getExistingRolesForDefaultRole(String site, String defaultRoleName) {
		return 0;
	}

	
}
