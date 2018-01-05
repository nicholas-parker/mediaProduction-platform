package com.mwt.production;

import org.alfresco.service.namespace.QName;

public class ProductionDocumentModel {

	// namespaces, types
	public static String AspectName = "document";
	public static String prefix = "prod";
	public static String productionModelURI = "http://nvp.com/model/mediaProduction/1.0";
	
	public static QName QN_PROD_DOCUMENT_ASPECT = QName.createQName(productionModelURI, AspectName);
	public static QName QN_DOC_TYPE = QName.createQName(productionModelURI, "docName");
	public static QName QN_DOC_CATEGORY = QName.createQName(productionModelURI, "docCategory");
	public static QName QN_LEGAL_ISSUING_COUNTRY = QName.createQName(productionModelURI, "legalIssuingCountry");
	public static QName QN_DOC_VALID_FROM = QName.createQName(productionModelURI, "docValidFrom");
	public static QName QN_DOC_VALID_TO = QName.createQName(productionModelURI, "docValidTo");
	public static QName QN_ISSUER_DOC_REF_NUMBER = QName.createQName(productionModelURI, "issuerDocRefNumber");
	public static QName QN_RECIPIENT_DOC_REF_NUMBER = QName.createQName(productionModelURI, "recipientDocRefNumber");
	
	public static String[] flatPropertyNames = { "prod_docType", "prod_docCategory", "prod_legalIssuingCountry",
			                                     "prod_docValidFrom", "prod_docValidTo", "prod_issuerDocRefNumber",
			                                     "prod_recipientDocRefNumber" };
	
}
