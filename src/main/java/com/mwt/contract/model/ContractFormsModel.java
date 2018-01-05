package com.mwt.contract.model;

import org.alfresco.service.namespace.QName;

public class ContractFormsModel {
   
	// namespaces, types
	public static String setupContractDetailsTask = "setupContractDetailsTask";
	public static String reviewContractDetailsTask = "reviewContractDetailsTask";
	public static String prefix = "mwtwf";
	public static String modelURI = "http://com.mwt/model/workflow/1.0";
	
	public static String PROP_NEW_SUPPLIER_FIRST_NAME = "newSupplierFirstName";
	public static String PROP_NEW_SUPPLIER_LAST_NAME = "newSupplierLastName";
	public static String PROP_NEW_SUPPLIER_EMAIL = "newSupplierEmail";

	public static QName QNAME_NEW_SUPPLIER_FIRST_NAME = QName.createQName(modelURI, PROP_NEW_SUPPLIER_FIRST_NAME);
	public static QName QNAME_NEW_SUPPLIER_LAST_NAME = QName.createQName(modelURI, PROP_NEW_SUPPLIER_LAST_NAME);
	public static QName QNAME_NEW_SUPPLIER_EMAIL = QName.createQName(modelURI, PROP_NEW_SUPPLIER_EMAIL);

}
