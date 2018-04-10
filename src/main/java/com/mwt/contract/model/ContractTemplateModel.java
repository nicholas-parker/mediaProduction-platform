package com.mwt.contract.model; 
 
import org.alfresco.service.namespace.QName;

public class ContractTemplateModel {


	// namespaces, types
	public static String ContractModelName = "Contract Template";
	public static String contractTemplateAspectName = "contractTemplate";
	public static String prefix = "contract";
	public static String contractModelURI = "http://mwt.com/model/contract/1.0";
	public static String CONTRACT_CONTAINER_NAME = "Contract Templates";
	public static String CONTRACT_CONTAINER_ID = "contractTemplateContainer";
	
	// local field names, useful for JSON to object
	public static String PROP_TEMPLATE_PAYE_STATUS = "templatePAYEStatus";
	public static String PROP_DELIVERABLE_TYPE = "deliverableType";
	public static String PROP_PRODUCTION_ROLE_TYPE = "productionRoleType";

	// QNames, useful for node properties
	public static QName QN_CONTRACT_TEMPLATE_ASPECT = QName.createQName(contractModelURI, contractTemplateAspectName);
	public static QName QN_TEMPLATE_PAYE_STATUS = QName.createQName(contractModelURI, PROP_TEMPLATE_PAYE_STATUS);
	public static QName QN_DELIVERABLE_TYPE =QName.createQName(contractModelURI, PROP_DELIVERABLE_TYPE);
	public static QName QN_PRODUCTION_ROLE_TYPE = QName.createQName(contractModelURI, PROP_PRODUCTION_ROLE_TYPE);
	
	public static QName QN_CONTRACT_CONTAINER_TYPE = QName.createQName(contractModelURI, CONTRACT_CONTAINER_ID);


}
