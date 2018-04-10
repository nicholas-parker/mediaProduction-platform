package com.mwt.contract.model;

import org.alfresco.service.namespace.QName;

public class crewNaturalExpensesModel {
	
	public static String contractAspectName = "crewNaturalExpensesModel";
	public static String prefix = "contract";
	public static String contractModelURI = "http://mwt.com/model/contract/1.0";

	public static String PROP_CONTRACT_PER_DIEM = "contractPerDiem";
	public static String PROP_CONTRACT_GENERAL_EXPENSES = "contractGeneralExpenses";
	public static String PROP_CONTRACT_TELEPHONE_EXPENSES_AVAILABLE = "contractTelephoneExpensesAvailable";
	
	public static QName QN_CONTRACT_PER_DIEM = QName.createQName(contractModelURI, PROP_CONTRACT_PER_DIEM);
	public static QName QN_CONTRACT_GENERAL_EXPENSES = QName.createQName(contractModelURI, PROP_CONTRACT_GENERAL_EXPENSES);
	public static QName QN_CONTRACT_TELEPHONE_EXPENSES_AVAILABLE = QName.createQName(contractModelURI, PROP_CONTRACT_TELEPHONE_EXPENSES_AVAILABLE);

}
