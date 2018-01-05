package com.mwt.contract.model;
 
import org.alfresco.service.namespace.QName;

public class INdividu {

	public static String servicePeriodAspectName = "servicePeriod";
	public static String prefix = "contract";
	public static String contractModelURI = "http://nvp.com/model/contract/1.0";
	
	public static String PROP_LOCAL_SERVICE_NAME = "serviceName";
	public static String PROP_LOCAL_SERVICE_DESCRIPTION = "serviceDescription";
	public static String PROP_LOCAL_SERVICE_TYPE_CODE = "serviceTypeCode";
	public static String PROP_LOCAL_SERVICE_START = "serviceStart";
	public static String PROP_LOCAL_SERVICE_END = "serviceEnd";

	public static QName QN_SERVICE_PERIOD_ASPECT = QName.createQName(contractModelURI, servicePeriodAspectName);
	public static QName QN_SERVICE_NAME = QName.createQName(contractModelURI, PROP_LOCAL_SERVICE_NAME);
	public static QName QN_SERVICE_DESCRIPTION = QName.createQName(contractModelURI, PROP_LOCAL_SERVICE_DESCRIPTION);
	public static QName QN_SERVICE_TYPE_CODE = QName.createQName(contractModelURI, PROP_LOCAL_SERVICE_TYPE_CODE);
	public static QName QN_SERVICE_START = QName.createQName(contractModelURI, PROP_LOCAL_SERVICE_START);
	public static QName QN_SERVICE_END = QName.createQName(contractModelURI, PROP_LOCAL_SERVICE_END);
	
}
