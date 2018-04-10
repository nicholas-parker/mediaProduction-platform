package com.mwt.roles;  

import org.alfresco.service.namespace.QName;

public class DefaultRoleModel { 

	// namespaces, types
	public static String DefaultRoleListName = "defaultRoles";
	public static String defaultRoleTypeName = "productionRoleType";
	public static String prefix = "nvpList";
	public static String roleTypeURI = "http://mwt.com/model/datalist/1.0";
	public static String PAYMENT_PAYE = "PAYE";
	public static String PAYMENT_SELF_EMPLOYED = "Self Employed";
	
	// local field names, useful for JSON to object•••••
	public static String PROP_ROLE_TYPE = "roleType";
	public static String PROP_TYPE_NAME = "typeName";
	public static String PROP_TYPE_DESCRIPTION = "typeDescription";
	public static String PROP_DELIVERABLE_TYPE = "typeDeliverableType";
	public static String PROP_CHARGE_CODE = "typeChargeCode";
	public static String PROP_PAYE_STATUS = "typePAYEStatus";
	public static String PROP_WORKING_WEEK = "typeWorkingWeek";
	public static String PROP_BUDGET_MIN = "typeBudgetMin";
	public static String PROP_BUDGET_MAX = "typeBudgetMax";
	public static String PROP_PAYMENT_PERIOD = "typePaymentPeriod";
	public static String PROP_RATE_PERIOD = "typeRatePeriod";
	public static String PROP_CURRENCY = "typeCurrency";
	public static String PROP_PROCESS_NAME = "typeProcessName";
	public static String PROP_ADMINISTRATION_TEAM = "typeAdministrationTeam";  
	public static String PROP_CONTRACT_TEMPLATE = "contractTemplate";
	public static String PROP_OVERTIME_PAYABLE = "overtimePayable";
	public static String PROP_OVERTIME_RATE = "overtimeRate";
	public static String PROP_NOTICE_PERIOD = "noticePeriod";
	public static String PROP_DAYS_PAID_HOLIDAY = "daysPaidHoliday";
	public static String PROP_TYPE_CATEGORY = "typeCategory";
	
	// QNames, useful for node properties
	public static QName QN_DEFAULT_ROLE = QName.createQName(roleTypeURI, defaultRoleTypeName);
	public static QName QN_ROLE_TYPE = QName.createQName(roleTypeURI, PROP_ROLE_TYPE);
	public static QName QN_TYPE_NAME = QName.createQName(roleTypeURI, PROP_TYPE_NAME);
	public static QName QN_TYPE_DESCRIPTION = QName.createQName(roleTypeURI, PROP_TYPE_DESCRIPTION);
	public static QName QN_DELIVERABLE_TYPE = QName.createQName(roleTypeURI, PROP_DELIVERABLE_TYPE);
	public static QName QN_CHARGE_CODE = QName.createQName(roleTypeURI, PROP_CHARGE_CODE);
	public static QName QN_PAYE_STATUS = QName.createQName(roleTypeURI, PROP_PAYE_STATUS);
	public static QName QN_WORKING_WEEK = QName.createQName(roleTypeURI, PROP_WORKING_WEEK);
	public static QName QN_BUDGET_MIN = QName.createQName(roleTypeURI, PROP_BUDGET_MIN);
	public static QName QN_BUDGET_MAX = QName.createQName(roleTypeURI, PROP_BUDGET_MAX);
	public static QName QN_PAYMENT_PERIOD = QName.createQName(roleTypeURI, PROP_PAYMENT_PERIOD);
	public static QName QN_RATE_PERIOD = QName.createQName(roleTypeURI, PROP_RATE_PERIOD);
	public static QName QN_CURRENCY = QName.createQName(roleTypeURI, PROP_CURRENCY);
	public static QName QN_PROCESS_NAME = QName.createQName(roleTypeURI, PROP_PROCESS_NAME);
	public static QName QN_ADMINISTRATION_TEAM = QName.createQName(roleTypeURI, PROP_ADMINISTRATION_TEAM);
	public static QName QN_CONTRACT_TEMPLATE = QName.createQName(roleTypeURI, PROP_CONTRACT_TEMPLATE);
	public static QName QN_OVERTIME_PAYABLE = QName.createQName(roleTypeURI, PROP_OVERTIME_PAYABLE);
	public static QName QN_OVERTIME_RATE = QName.createQName(roleTypeURI, PROP_OVERTIME_RATE);
	public static QName QN_NOTICE_PERIOD = QName.createQName(roleTypeURI, PROP_NOTICE_PERIOD);
	public static QName QN_DAYS_PAID_HOLIDAY = QName.createQName(roleTypeURI, PROP_DAYS_PAID_HOLIDAY);
	public static QName QN_TYPE_CATEGORY = QName.createQName(roleTypeURI, PROP_TYPE_CATEGORY);
}
