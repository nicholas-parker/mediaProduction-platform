package com.mwt.alfresco.model;

import org.alfresco.service.namespace.QName;

public class PersonModel {
 
	// namespaces, types
	public static String personName = "person";
	public static String prefix = "cm";
	public static String contentURI = "http://www.alfresco.org/model/content/1.0";
	
	// local field names, useful for JSON to object
	public static String PROP_USER_NAME = "userName";
	public static String PROP_FIRST_NAME = "firstName";
	public static String PROP_MIDDLE_NAME = "middleName";
	public static String PROP_LAST_NAME = "lastName";
	public static String PROP_EMAIL = "email";
	public static String PROP_ORGANISATION_ID = "organisationId";
	public static String PROP_ORGANISATION = "organisation";
	public static String PROP_LOCATION = "location";
	public static String PROP_COMPANYADDRESS1 = "companyaddress1";
	public static String PROP_COMPANYADDRESS2 = "companyaddress2";
	public static String PROP_COMPANYADDRESS3 = "companyaddress3";
	public static String PROP_COMPANYPOSTCODE = "companypostcode";
	public static String PROP_COMPANYTELEPHONE = "companytelephone";
	
	public static QName QN_USER_NAME = QName.createQName(contentURI, PROP_USER_NAME );
	public static QName QN_FIRST_NAME = QName.createQName(contentURI, PROP_FIRST_NAME );
	public static QName QN_MIDDLE_NAME = QName.createQName(contentURI, PROP_MIDDLE_NAME );
	public static QName QN_LAST_NAME = QName.createQName(contentURI, PROP_LAST_NAME );
	public static QName QN_EMAIL = QName.createQName(contentURI, PROP_EMAIL );
	public static QName QN_ORGANISATION_ID = QName.createQName(contentURI, PROP_ORGANISATION_ID);
	public static QName QN_ORGANISATION = QName.createQName(contentURI, PROP_ORGANISATION);
	public static QName QN_LOCATION = QName.createQName(contentURI, PROP_LOCATION);
	public static QName QN_COMPANYADDRESS1 = QName.createQName(contentURI, PROP_COMPANYADDRESS1);
	public static QName QN_COMPANYADDRESS2 = QName.createQName(contentURI, PROP_COMPANYADDRESS2);
	public static QName QN_COMPANYADDRESS3 = QName.createQName(contentURI, PROP_COMPANYADDRESS3);
	public static QName QN_COMPANYPOSTCODE = QName.createQName(contentURI, PROP_COMPANYPOSTCODE);
	public static QName QN_COMPANYTELEPHONE = QName.createQName(contentURI, PROP_COMPANYTELEPHONE);
	
}
