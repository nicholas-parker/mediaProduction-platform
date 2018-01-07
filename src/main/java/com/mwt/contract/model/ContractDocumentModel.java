package com.mwt.contract.model;
 
import org.alfresco.service.namespace.QName;
import java.util.HashMap;
import java.io.Serializable;

/**
 * Java model for Contract aspect
 * 
 * @author nickp
 *  
 */
public class ContractDocumentModel extends HashMap<QName, Serializable> {

	// namespaces, types
	public static String ContractModelName = "Contract";
	public static String contractAspectName = "contractDocument";
	public static String prefix = "contract";
	public static String contractModelURI = "http://nvp.com/model/contract/1.0";
	
	// document approval 
	public static String CONTRACT_APPROVAL_APPROVED = "APPROVED";
	public static String CONTRACT_APPROVAL_NOT_APPROVED = "NOT APPROVED";
	
	// local field names, useful for JSON to object
	public static String PROP_CONTRACT_DATE = "contractDate";
	public static String PROP_CONTRACT_CODE = "contractCode";
	public static String PROP_CONTRACT_APPROVAL_ROLE = "contractApprovalRole";
	public static String PROP_CONTRACT_APPROVAL_STATUS = "contractApprovalStatus";
	public static String PROP_CONTRACT_APPROVAL_DATE = "contractApprovalDate";
	public static String PROP_SIGNING_TRANSACTION_ID = "signingTransactionId";
	public static String PROP_PAYE_STATUS = "PAYEstatus";
	public static String PROP_CONTRACT_VALUE = "contractValue";
	public static String PROP_RATE_PERIOD_SPECIFIER = "ratePeriodSpecifier";
	public static String PROP_CONTRACT_VALUE_CURRENCY = "contractValueCurrency";
	public static String PROP_WORKING_WEEK = "workingWeek";
	public static String PROP_CONTRACT_DELIVERABLE_TYPE = "contractDeliverableType";
	public static String PROP_PAYMENT_PERIOD_SPECIFIER = "paymentPeriodSpecifier";
	public static String PROP_CONTRACT_OVERTIME_PAYABLE = "overtimePayable";
	public static String PROP_CONTRACT_OVERTIME_RATE = "overtimeRate";
	public static String PROP_NOTICE_PERIOD = "noticePeriod";
	public static String PROP_LOCATION = "location";
	public static String PROP_CONTRACT_PARTIES = "contractParties";
	public static String PROP_PRODUCTION_ROLE = "productionRole";
    public static String PROP_SUPPLIER = "contractSupplier";
    public static String PROP_HOLIDAY_RATE = "holidayRate";
    
    
	// QNames, useful for node properties
	public static QName QN_CONTRACT_ASPECT = QName.createQName(contractModelURI, contractAspectName);
	public static QName QN_CONTRACT_DATE = QName.createQName(contractModelURI, PROP_CONTRACT_DATE);
	public static QName QN_CONTRACT_CODE = QName.createQName(contractModelURI, PROP_CONTRACT_CODE);
	public static QName QN_CONTRACT_APPROVAL_ROLE = QName.createQName(contractModelURI, PROP_CONTRACT_APPROVAL_ROLE);
	public static QName QN_CONTRACT_APPROVAL_STATUS =QName.createQName(contractModelURI, PROP_CONTRACT_APPROVAL_STATUS);
	public static QName QN_CONTRACT_APPROVAL_DATE = QName.createQName(contractModelURI, PROP_CONTRACT_APPROVAL_DATE);
	public static QName QN_SIGNING_TRANSACTION_ID = QName.createQName(contractModelURI, PROP_SIGNING_TRANSACTION_ID);
	public static QName QN_PAYE_STATUS = QName.createQName(contractModelURI, PROP_PAYE_STATUS);
	public static QName QN_CONTRACT_VALUE = QName.createQName(contractModelURI, PROP_CONTRACT_VALUE);
	public static QName QN_RATE_PERIOD_SPECIFIER = QName.createQName(contractModelURI, PROP_RATE_PERIOD_SPECIFIER);
	public static QName QN_CONTRACT_VALUE_CURRENCY = QName.createQName(contractModelURI, PROP_CONTRACT_VALUE_CURRENCY);
	public static QName QN_WORKING_WEEK = QName.createQName(contractModelURI, PROP_WORKING_WEEK);
	public static QName QN_CONTRACT_DELIVERABLE_TYPE = QName.createQName(contractModelURI, PROP_CONTRACT_DELIVERABLE_TYPE);
	public static QName QN_CONTRACT_PAYMENT_PERIOD_SPECIFIER = QName.createQName(contractModelURI, PROP_PAYMENT_PERIOD_SPECIFIER);
	public static QName QN_OVERTIME_PAYABLE = QName.createQName(contractModelURI, PROP_CONTRACT_OVERTIME_PAYABLE);
	public static QName QN_OVERTIME_RATE = QName.createQName(contractModelURI, PROP_CONTRACT_OVERTIME_RATE);
	public static QName QN_CONTACT_NOTICE_PERIOD = QName.createQName(contractModelURI, PROP_NOTICE_PERIOD);
	public static QName QN_LOCATION = QName.createQName(contractModelURI, PROP_LOCATION);
	public static QName QN_CONTRACT_PARTIES = QName.createQName(contractModelURI, PROP_CONTRACT_PARTIES);
	public static QName QN_PRODUCTION_ROLE = QName.createQName(contractModelURI, PROP_PRODUCTION_ROLE);
    public static QName QN_SUPPLIER = QName.createQName(contractModelURI, PROP_SUPPLIER);
    public static QName QN_HOLIDAY_RATE = QName.createQName(contractModelURI, PROP_HOLIDAY_RATE);
    
	/** payment period */
    public static String RATE_PERIOD_PER_HOUR = "per hour";
    public static String RATE_PERIOD_PER_DAY = "per day";
    public static String RATE_PERIOD_PER_WEEK = "per week";
    public static String RATE_PERIOD_PER_MONTH = "per month";
    
    /** type of the container where we keep contracts in a production */
    public static QName QN_CONTRACT_CONTAINER_TYPE = QName.createQName(contractModelURI, "contractContainer");
    public static String CONTRACT_CONTAINER_NAME = "Contracts";
    public static String CONTRACT_CONTAINER_ID = "contractContainer";
    
}
