package com.mwt.contract.model; 

import org.alfresco.service.namespace.QName;

public class BankAccountModel {
	
	// namespaces, types
	public static String contractAspectName = "bankAccount";
	public static String prefix = "prod";
	public static String contractModelURI = "http://nvp.com/model/mediaProduction/1.0";
	
	public static QName QN_BANKACCOUNT_ASPECT = QName.createQName(contractModelURI, contractAspectName);
	
	public static QName QN_BANK_NAME = QName.createQName(contractModelURI, "bankName");
	public static QName QN_BANK_ACCOUNT_NAME = QName.createQName(contractModelURI, "bankAccountName");
	public static QName QN_BANK_ACCOUNT_NUMBER = QName.createQName(contractModelURI, "bankAccountNumber");
	public static QName QN_BANK_BRANCH_SORT_CODE = QName.createQName(contractModelURI, "bankBranchSortCode");

}
