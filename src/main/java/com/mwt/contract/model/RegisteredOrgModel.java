package com.mwt.contract.model;

import org.alfresco.service.namespace.QName;

public class RegisteredOrgModel {

	// namespaces, types
	public static String aspectName = "registeredOrg";
	public static String prefix = "contract";
	public static String registeredOrgModelURI = "http://nvp.com/model/contract/1.0";
	
	// local field names, useful for JSON to object
	public static String PROP_REGISTERED_NUMBER = "registeredNum";
	public static String PROP_REGISTER_ORG = "registerOrg";
	public static String PROP_REGISTER_REGION = "registerRegion";
	public static String PROP_TAX_NUMBER = "taxNumber";
	public static String PROP_TAX_REGION = "taxRegion";
	
	// QNames, useful for node properties
	public static QName QN_REGISTERED_ORG_MODEL = QName.createQName(registeredOrgModelURI, aspectName);
	public static QName QN_REGISTERED_NUMBER = QName.createQName(registeredOrgModelURI, PROP_REGISTERED_NUMBER);
	public static QName QN_REGISTERED_ORG =QName.createQName(registeredOrgModelURI, PROP_REGISTER_ORG);
	public static QName QN_REGISTER_REGION = QName.createQName(registeredOrgModelURI, PROP_REGISTER_REGION);
	public static QName QN_TAX_NUMBER = QName.createQName(registeredOrgModelURI, PROP_TAX_NUMBER);
	public static QName QN_TAX_REGION = QName.createQName(registeredOrgModelURI, PROP_TAX_REGION);

}
