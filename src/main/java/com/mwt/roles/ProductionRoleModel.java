package com.mwt.roles;   

import org.alfresco.service.namespace.QName;
 
public class ProductionRoleModel {
	
	// namespaces, types
	public static String RoleListName = "productionRoles";
	public static String productionRoleTypeName = "productionRole";
	public static String prefix = "nvpList";
	public static String roleItemURI = "http://nvp.com/model/datalist/1.0";
	public static String PRODUCTION_ROLE_LIST_NAME = "productionRoles";
	
	// local field names, useful for JSON to object
	public static String PROP_ROLE_TYPE = "roleType";
	public static String PROP_ROLE_NAME = "roleName";
	public static String PROP_CREATED_DATE = "createdDate";
	public static String PROP_OFFER_DATE= "offerDate";
	public static String PROP_ACCEPTED_DATE = "acceptedDate";
	public static String PROP_ROLE_STATUS = "roleStatus";
	public static String PROP_CONTRACT_PROCESS_ID = "contractProcessId";
	public static String PROP_ASSIGNEE = "assignee";
	public static String PROP_START_DATE = "startDate";
	public static String PROP_END_DATE = "endDate";
	public static String PROP_CHARGE_CODE = "chargeCode";
	public static String PROP_CONTRACT_DOCUMENTS = "roleContractDocuments";
	public static String PROP_MIN_BUDGET = "budgetMin";
	public static String PROP_MAX_BUDGET = "budgetMax";
	public static String PROP_TOTAL_CONTRACTS_AMOUNT = "totalContractsAmount";
	public static String PROP_CURRENCY = "currency";
	public static String PROP_PAYE_STATUS = "PAYEStatus";
	
	public static String PROP_ASSOC_ASSIGNEE = "assignee";
	public static String PROP_ASSOC_ROLE_CONTRACT_DOCUMENTS = "roleContractDocuments";
	
	// QNames, useful for node properties  
	public static QName QN_PRODUCTION_ROLE = QName.createQName(roleItemURI, productionRoleTypeName);
	public static QName QN_ROLE_TYPE = QName.createQName(roleItemURI, PROP_ROLE_TYPE);
	public static QName QN_ROLE_NAME = QName.createQName(roleItemURI, PROP_ROLE_NAME);
	public static QName QN_CREATED_DATE = QName.createQName(roleItemURI, PROP_CREATED_DATE);
	public static QName QN_OFFER_DATE= QName.createQName(roleItemURI, PROP_OFFER_DATE);
	public static QName QN_ACCEPTED_DATE = QName.createQName(roleItemURI, PROP_ACCEPTED_DATE);
	public static QName QN_ROLE_STATUS = QName.createQName(roleItemURI, PROP_ROLE_STATUS);
	public static QName QN_CONTRACT_PROCESS_ID = QName.createQName(roleItemURI, PROP_CONTRACT_PROCESS_ID);
	public static QName QN_ASSIGNEE = QName.createQName(roleItemURI, PROP_ASSIGNEE);
	public static QName QN_START_DATE = QName.createQName(roleItemURI, PROP_START_DATE);
	public static QName QN_END_DATE = QName.createQName(roleItemURI, PROP_END_DATE);
	public static QName QN_CHARGE_CODE = QName.createQName(roleItemURI, PROP_CHARGE_CODE);
	public static QName QN_CONTRACT_DOCUMENTS = QName.createQName(roleItemURI, PROP_CONTRACT_DOCUMENTS);
	public static QName QN_MIN_BUDGET = QName.createQName(roleItemURI, PROP_MIN_BUDGET);
	public static QName QN_MAX_BUDGET = QName.createQName(roleItemURI, PROP_MAX_BUDGET);
	public static QName QN_TOTAL_CONTRACTS_AMOUNT = QName.createQName(roleItemURI, PROP_TOTAL_CONTRACTS_AMOUNT);
	public static QName QN_CURRENCY = QName.createQName(roleItemURI, PROP_CURRENCY);
	public static QName QN_PAYE_STATUS = QName.createQName(roleItemURI, PROP_PAYE_STATUS);
    public static QName QN_ROLE_NODEREF = QName.createQName(roleItemURI, "roleNodeRef");
    public static QName QN_TOTAL_BUDGET_MIN = QName.createQName(roleItemURI, "totalBudgetMin");
    public static QName QN_TOTAL_BUDGET_MAX = QName.createQName(roleItemURI, "totalBudgetMax");
    public static QName QN_RATE_PERIOD = QName.createQName(roleItemURI, "ratePeriod");
    public static QName QN_PAYMENT_PERIOD = QName.createQName(roleItemURI, "paymentPeriod");
    public static QName QN_ROLE_DESCRIPTION = QName.createQName(roleItemURI, "roleDescription");
    
    // associations
    public static QName QN_ASSOC_ASSIGNEE = QName.createQName(roleItemURI, PROP_ASSOC_ASSIGNEE);
    public static QName QN_ASSOC_ROLE_CONTRACT_DOCUMENTS = QName.createQName(roleItemURI, PROP_ASSOC_ROLE_CONTRACT_DOCUMENTS);
    
	// create/update field names
	public static String FORM_PROP_ALF_DESINATION = "alf_destination";
	public static String FORM_PROP_ROLE_TYPE = "prop_nvpList_roleType";
	public static String FORM_PROP_ROLE_NAME = "prop_nvpList_roleName";
	public static String FORM_PROP_ROLE_STATUS = "prop_nvpList_roleStatus";
	public static String FORM_PROP_ROLE_ASSIGNEE = "prop_nvpList_assignee";
	public static String FORM_PROP_START_DATE = "prop_nvpList_startDate";
	public static String FORM_PROP_END_DATE = "prop_nvpList_endDate";
	
	
	// attribute values
	public static String VALUE_DEFAULT_ROLE_TYPE = "DEFAULT";
	public static String ROLE_STATUS_SETUP = "Set up";
	public static String ROLE_STATUS_SUPPLIER_REVIEW = "Supplier review";
	public static String ROLE_STATUS_ACCEPTED = "Accepted";
	public static String ROLE_STATUS_DECLINED = "Declined";
	public static String ROLE_STATUS_FINAL_APPROVAL = "Final approval";
	public static String ROLE_STATUS_APPROVED = "Approved";
	public static String ROLE_STATUS_ACTIVE = "Active";
	public static String ROLE_STATUS_COMPLETED = "COmpleted";
	public static String ROLE_STATUS_NEW_SUPPLIER = "New supplier";
	public static String ROLE_STATUS_CANCELED = "Canceled";
	
	
}
