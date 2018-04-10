package com.mwt.contract.model;

import org.alfresco.service.namespace.QName;

/**
 * This class represents the fields in the 
 * role-offer-contract.ReviewContractDetails process task
 * 
 * data model: mwtwf:reviewContractDetailsTask
 * 
 * @author nickp
 *
 */
public class RightToWorkModel {

	// namespaces, types
	public static String ModelName = "Right to work";
	public static String rightToWorkAspectName = "rightToWork";
	public static String prefix = "contract";
	public static String modelURI = "http://mwt.com/model/contract/1.0";
	
	public static String PROP_DATE_OF_BIRTH = "dateOfBirth";
	public static String PROP_TOWN_OF_BIRTH = "townOfBirth";
	public static String PROP_COUNTRY_OF_BIRTH = "countryOfBirth";
	public static String PROP_NATIONAL_INSURANCE_NUM = "nationalInsuranceNumber";
	public static String PROP_IDENTITY_DOCUMENT_REFERENCE = "identityDOcumentReference";
	public static String PROP_IDENTITY_DOCUMENT_TYPE = "identityDocumentType";
	public static String PROP_IDENTITY_DOCUMENT_ISSUER = "identityDocumentIssuer";
	public static String PROP_VISA_NUMBER = "visaNumber";
	public static String PROP_VISA_EXPIRY_DATE = "visaExpiryDate";
	public static String PROP_RIGHT_TO_WORK_ASSERTED = "rightToWorkAsserted";
	public static String PROP_RIGHT_TO_WORK_APPROVED = "rightToWorkApproved";
	public static String PROP_RIGHT_TO_APPROVED_DATE = "rightToWorkApprovedDate";
	public static String PROP_RIGHT_TO_RENEW_DATE = "rightToWorkRenewDate";
	public static String PROP_RIGHT_TO_EXPIRE_DATE = "rightToWorkExpireDate";
	
	public static QName QNAME_RIGHT_TO_WORK_ASPECT = QName.createQName(modelURI, rightToWorkAspectName);
	public static QName QNAME_DATE_OF_BIRTH = QName.createQName(modelURI, "dateOfBirth");
	public static QName QNAME_TOWN_OF_BIRTH = QName.createQName(modelURI, "townOfBirth");
	public static QName QNAME_COUNTRY_OF_BIRTH = QName.createQName(modelURI, "countryOfBirth");
	public static QName QNAME_NATIONAL_INSURANCE_NUM = QName.createQName(modelURI, "nationalInsuranceNumber");
	public static QName QNAME_IDENTITY_DOCUMENT_REFERENCE = QName.createQName(modelURI, "identityDOcumentReference");
	public static QName QNAME_IDENTITY_DOCUMENT_TYPE = QName.createQName(modelURI, "identityDocumentType");
	public static QName QNAME_IDENTITY_DOCUMENT_ISSUER = QName.createQName(modelURI, "identityDocumentIssuer");
	public static QName QNAME_VISA_NUMBER = QName.createQName(modelURI, "visaNumber");
	public static QName QNAME_VISA_EXPIRY_DATE = QName.createQName(modelURI, "visaExpiryDate");
	public static QName QNAME_RIGHT_TO_WORK_ASSERTED = QName.createQName(modelURI, "rightToWorkAsserted");
	public static QName QNAME_RIGHT_TO_WORK_APPROVED = QName.createQName(modelURI, "rightToWorkApproved");
	public static QName QNAME_RIGHT_TO_APPROVED_DATE = QName.createQName(modelURI, "rightToWorkApprovedDate");
	public static QName QNAME_RIGHT_TO_RENEW_DATE = QName.createQName(modelURI, "rightToWorkRenewDate");
	public static QName QNAME_RIGHT_TO_EXPIRE_DATE = QName.createQName(modelURI, "rightToWorkExpireDate");
	
}
