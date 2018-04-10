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
	public static String contractModelURI = "http://mwt.com/model/contract/1.0";
	
	// document lifecycle status
	public static String CONTRACT_STATUS_PREPARING = "In preparation";
	public static String CONTRACT_STATUS_PENDING_APPROVAL = "Pending approval";
	public static String CONTRACT_STATUS_PREPARED = "Prepared";
	public static String CONTRACT_STATUS_REVIEWING = "With supplier for review";
	public static String CONTRACT_STATUS_SUPPLIER_ACCEPTED = "Supplier accepted";
	public static String CONTRACT_STATUS_SUPPLIER_DECLINED = "Supplier declined";
	public static String CONTRACT_STATUS_FINAL_APPROVAL = "Approved";
	public static String CONTRACT_STATUS_FINAL_REJECTED = "Rejected";
	public static String CONTRACT_STATUS_WITHDRAWN = "Withdrawn";
	
	// local field names, useful for JSON to object	
	public static String PROP_CONTRACT_DATE = "contractDate";
	public static String PROP_CONTRACT_CODE = "contractCode";
	public static String PROP_CONTRACT_APPROVAL_ROLE = "contractApprovalRole";
	public static String PROP_CONTRACT_APPROVAL_STATUS = "contractApprovalStatus";
	public static String PROP_CONTRACT_APPROVAL_DATE = "contractApprovalDate";
	public static String PROP_SIGNING_TRANSACTION_ID = "signingTransactionId";
	public static String PROP_CONTRACT_VALUE = "contractValue";
	public static String PROP_CONTRACT_DELIVERABLE_TYPE =	 "contractDeliverableType";	
	

	// QNames, useful for node properties
	public static QName QN_CONTRACT_ASPECT = QName.createQName(contractModelURI, contractAspectName);
	public static QName QN_CONTRACT_DATE = QName.createQName(contractModelURI, PROP_CONTRACT_DATE);
	public static QName QN_CONTRACT_CODE = QName.createQName(contractModelURI, PROP_CONTRACT_CODE);
	public static QName QN_CONTRACT_APPROVAL_ROLE = QName.createQName(contractModelURI, PROP_CONTRACT_APPROVAL_ROLE);
	public static QName QN_CONTRACT_APPROVAL_STATUS =QName.createQName(contractModelURI, PROP_CONTRACT_APPROVAL_STATUS);
	public static QName QN_CONTRACT_APPROVAL_DATE = QName.createQName(contractModelURI, PROP_CONTRACT_APPROVAL_DATE);
	public static QName QN_CONTRACT_VALUE = QName.createQName(contractModelURI, PROP_CONTRACT_VALUE);
	public static QName QN_SIGNING_TRANSACTION_ID = QName.createQName(contractModelURI, PROP_SIGNING_TRANSACTION_ID);
	public static QName QN_CONTRACT_DELIVERABLE_TYPE = QName.createQName(contractModelURI, PROP_CONTRACT_DELIVERABLE_TYPE);
	
	
	
    /** type of the container where we keep contracts in a production */
    public static QName QN_CONTRACT_CONTAINER_TYPE = QName.createQName(contractModelURI, "contractContainer");
    public static String CONTRACT_CONTAINER_NAME = "Contracts";
    public static String CONTRACT_CONTAINER_ID = "contractContainer";
   
    /** the variable name of the process variable which contains a contract document node id */
    public static String CONTRACT_DOCUMENT_NODE_ID = "contract_contractDocumentNodeId";
    
}
