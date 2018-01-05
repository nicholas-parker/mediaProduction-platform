package com.mwt.production;

import org.alfresco.service.namespace.QName;

public class MediaProductionOrganisationModel {

	
	public static String aspectName = "mediaProduction";
	public static String prefix = "prod";
	public static String productionModelURI = "http://nvp.com/model/mediaProduction/1.0";
	
	public static String PROP_PRODUCTION_NAME = "productionName";
	public static String PROP_PRODUCTION_ORGANISATION_NAME = "productionOrganisationName";
	public static String PROP_PRODUCTION_REGISTRATION_NUMBER = "productionRegistrationNumber";
	public static String PROP_PRODUCTION_REGISTRATION_REGION = "productionRegistrationRegion";
	public static String PROP_PRODUCTION_VAT_NUMBER = "productionVATNumber";
	public static String PROP_PRODUCTION_ADDR1 = "productionAddress1";
	public static String PROP_PRODUCTION_ADDR2 = "productionAddress2";
	public static String PROP_PRODUCTION_ADDR3 = "productionAddress3";
	public static String PROP_PRODUCTION_PO_CODE = "productionPOCode";
	public static String PROP_PRODUCTION_COUNTRY = "productionCountry";
	
	public static QName QN_PRODUCTION_NAME = QName.createQName(productionModelURI, PROP_PRODUCTION_NAME);
	public static QName QN_PRODUCTION_ORGANISATION_NAME = QName.createQName(productionModelURI, PROP_PRODUCTION_ORGANISATION_NAME);
	public static QName QN_PRODUCTION_REGISTRATION_NUMBER = QName.createQName(productionModelURI, PROP_PRODUCTION_REGISTRATION_NUMBER);
	public static QName QN_PRODUCTION_REGISTRATION_REGION = QName.createQName(productionModelURI, PROP_PRODUCTION_REGISTRATION_REGION);
	public static QName QN_PRODUCTION_VAT_NUMBER = QName.createQName(productionModelURI, PROP_PRODUCTION_VAT_NUMBER);
	public static QName QN_PRODUCTION_ADDR1 = QName.createQName(productionModelURI, PROP_PRODUCTION_ADDR1);
	public static QName QN_PRODUCTION_ADDR2 = QName.createQName(productionModelURI, PROP_PRODUCTION_ADDR2);
	public static QName QN_PRODUCTION_ADDR3 = QName.createQName(productionModelURI, PROP_PRODUCTION_ADDR3);
	public static QName QN_PRODUCTION_PO_CODE = QName.createQName(productionModelURI, PROP_PRODUCTION_PO_CODE);
	public static QName QN_PRODUCTION_COUNTRY = QName.createQName(productionModelURI, PROP_PRODUCTION_COUNTRY);
	
}
