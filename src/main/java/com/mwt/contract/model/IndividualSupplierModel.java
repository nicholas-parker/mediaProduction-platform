package com.mwt.contract.model; 

import org.alfresco.service.namespace.QName;

public class IndividualSupplierModel {

	// namespaces, types
	public static String contractAspectName = "individualSupplier";
	public static String prefix = "contract";
	public static String contractModelURI = "http://mwt.com/model/contract/1.0";
	
	public static QName QN_INDIVIDUAL_SUPPLIER_ASPECT = QName.createQName(contractModelURI, contractAspectName);
	public static QName QN_SUPPLIER_FIRST_NAME = QName.createQName(contractModelURI, "supplierFirstName");
	public static QName QN_SUPPLIER_LAST_NAME = QName.createQName(contractModelURI, "supplierLastName");
	public static QName QN_SUPPLIER_EMAIL = QName.createQName(contractModelURI, "supplierEmail");
	public static QName QN_SUPPLIER_TELEPHONE = QName.createQName(contractModelURI, "supplierTelephone");
	public static QName QN_SUPPLIER_ADDRESS1 = QName.createQName(contractModelURI, "supplierAddress1");
	public static QName QN_SUPPLIER_ADDRESS2 = QName.createQName(contractModelURI, "supplierAddress2");
	public static QName QN_SUPPLIER_ADDRESS3 = QName.createQName(contractModelURI, "supplierAddress3");
	public static QName QN_SUPPLIER_POST_CODE = QName.createQName(contractModelURI, "supplierPostCode");

}
