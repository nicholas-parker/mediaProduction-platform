package com.mwt.contract.model;

import org.alfresco.service.namespace.QName;

public class RegisteredOrgModel {

	// namespaces, types
	public static String aspectName = "registeredOrg";
	public static String prefix = "contract";
	public static String registeredOrgModelURI = "http://mwt.com/model/contract/1.0";
	
	// local field names, useful for JSON to object
	public static String PROP_REGISTERED_NAME = "registeredName";
	public static String PROP_OPERATING_NAME = "operatingName";
	public static String PROP_FORMAL_ADDRESS = "formalAddress";
	public static String PROP_FORMAL_POST_CODE = "formalPostCode";
	public static String PROP_REGISTERED_NUMBER = "registeredNum";
	public static String PROP_REGISTER_REGION = "registerRegion";
	public static String PROP_TAX_NUMBER = "taxNumber";
	public static String PROP_TAX_REGION = "taxRegion";
    public static String PROP_ORG_CONTACT_EMAIL = "orgContactEmail";
    public static String PROP_ORG_CONTACT_TEL = "orgContactTel";
    
	// QNames, useful for node properties
    
	public static QName QN_REGISTERED_ORG_MODEL = QName.createQName(registeredOrgModelURI, aspectName);
	public static QName QN_REGISTERED_NAME = QName.createQName(registeredOrgModelURI, PROP_REGISTERED_NAME);
	public static QName QN_OPERATING_NAME = QName.createQName(registeredOrgModelURI, PROP_OPERATING_NAME);
	public static QName QN_FORMAL_ADDRESS = QName.createQName(registeredOrgModelURI, PROP_FORMAL_ADDRESS);
	public static QName QN_FORMAL_POST_CODE = QName.createQName(registeredOrgModelURI, PROP_FORMAL_POST_CODE);
	public static QName QN_REGISTERED_NUMBER = QName.createQName(registeredOrgModelURI, PROP_REGISTERED_NUMBER);
	public static QName QN_REGISTER_REGION = QName.createQName(registeredOrgModelURI, PROP_REGISTER_REGION);
	public static QName QN_TAX_NUMBER = QName.createQName(registeredOrgModelURI, PROP_TAX_NUMBER);
	public static QName QN_TAX_REGION = QName.createQName(registeredOrgModelURI, PROP_TAX_REGION);
	public static QName QN_ORG_CONTACT_EMAIL = QName.createQName(registeredOrgModelURI, PROP_ORG_CONTACT_EMAIL);
	public static QName QN_ORG_CONTACT_TEL = QName.createQName(registeredOrgModelURI, PROP_ORG_CONTACT_TEL);
}
