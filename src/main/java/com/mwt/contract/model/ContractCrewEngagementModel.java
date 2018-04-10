package com.mwt.contract.model;

import org.alfresco.service.namespace.QName;

public class ContractCrewEngagementModel {

	// namespaces, types
	public static String ContractModelName = "Contract";
	public static String contractAspectName = "crewEngagement";
	public static String prefix = "contract";
	public static String contractModelURI = "http://mwt.com/model/contract/1.0";

	public static String PROP_PAYE_STATUS = "PAYEstatus";
	public static String PROP_RATE_PERIOD_SPECIFIER = "ratePeriodSpecifier";
	public static String PROP_CONTRACT_VALUE_CURRENCY = "contractValueCurrency";
	public static String PROP_WORKING_WEEK = "workingWeek";
	public static String PROP_PAYMENT_PERIOD_SPECIFIER = "paymentPeriodSpecifier";
	public static String PROP_CONTRACT_OVERTIME_PAYABLE = "overtimePayable";
	public static String PROP_CONTRACT_OVERTIME_RATE = "overtimeRate";
	public static String PROP_NOTICE_PERIOD = "noticePeriod";
	public static String PROP_LOCATION = "location";
	public static String PROP_CONTRACT_PARTIES = "contractParties";
	public static String PROP_PRODUCTION_ROLE = "productionRole";
    public static String PROP_SUPPLIER = "contractSupplier";
    public static String PROP_HOLIDAY_RATE = "holidayRate";
    
	// local association names	
	public static String LOCAL_ASSOC_PRODUCTION_ROLE = "productionRole";
	public static String LOCAL_ASSOC_CONTRACT_SUPPLIER = "contractSupplier";
	public static String LOCAL_CHILD_ENGAGEMENT_PERIODS = "engagementPeriods";

    public static QName QN_CONTRACT_CREW_ENGAGEMENT_ASPECT = QName.createQName(contractModelURI, contractAspectName);
	public static QName QN_PAYE_STATUS = QName.createQName(contractModelURI, PROP_PAYE_STATUS);
	public static QName QN_RATE_PERIOD_SPECIFIER = QName.createQName(contractModelURI, PROP_RATE_PERIOD_SPECIFIER);
	public static QName QN_CONTRACT_VALUE_CURRENCY = QName.createQName(contractModelURI, PROP_CONTRACT_VALUE_CURRENCY);
	public static QName QN_WORKING_WEEK = QName.createQName(contractModelURI, PROP_WORKING_WEEK);
	public static QName QN_CONTRACT_PAYMENT_PERIOD_SPECIFIER = QName.createQName(contractModelURI, PROP_PAYMENT_PERIOD_SPECIFIER);
	public static QName QN_OVERTIME_PAYABLE = QName.createQName(contractModelURI, PROP_CONTRACT_OVERTIME_PAYABLE);
	public static QName QN_OVERTIME_RATE = QName.createQName(contractModelURI, PROP_CONTRACT_OVERTIME_RATE);
	public static QName QN_CONTACT_NOTICE_PERIOD = QName.createQName(contractModelURI, PROP_NOTICE_PERIOD);
	public static QName QN_LOCATION = QName.createQName(contractModelURI, PROP_LOCATION);
	public static QName QN_CONTRACT_PARTIES = QName.createQName(contractModelURI, PROP_CONTRACT_PARTIES);
	public static QName QN_PRODUCTION_ROLE = QName.createQName(contractModelURI, PROP_PRODUCTION_ROLE);
    public static QName QN_SUPPLIER = QName.createQName(contractModelURI, PROP_SUPPLIER);
    public static QName QN_HOLIDAY_RATE = QName.createQName(contractModelURI, PROP_HOLIDAY_RATE);

    public static QName QN_ASSOC_PRODUCTION_ROLE = QName.createQName(contractModelURI, LOCAL_ASSOC_PRODUCTION_ROLE);
	public static QName QN_ASSOC_CONTRACT_SUPPLIER = QName.createQName(contractModelURI, LOCAL_ASSOC_CONTRACT_SUPPLIER);
	public static QName QN_CHILD_ASSOC_ENGAGEMENT_PERIODS = QName.createQName(contractModelURI, LOCAL_CHILD_ENGAGEMENT_PERIODS);
	
	/** payment period */
    public static String RATE_PERIOD_PER_HOUR = "per hour";
    public static String RATE_PERIOD_PER_DAY = "per day";
    public static String RATE_PERIOD_PER_WEEK = "per week";
    public static String RATE_PERIOD_PER_MONTH = "per month";

}
