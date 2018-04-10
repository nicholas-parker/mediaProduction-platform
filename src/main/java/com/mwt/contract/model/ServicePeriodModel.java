package com.mwt.contract.model;

import java.util.Date;

import org.alfresco.service.namespace.QName;

public class ServicePeriodModel {

	// namespaces, types
	public static String servicePeriodTypeName = "servicePeriod";
	public static String prefix = "contract";
	public static String contractModelURI = "http://mwt.com/model/contract/1.0";
	
	public static QName QN_SERVICEPERIOD_TYPE = QName.createQName(contractModelURI, servicePeriodTypeName);
	
	public static QName QN_SERVICEPERIOD_ID = QName.createQName(contractModelURI, "servicePeriodId");
	public static QName QN_SERVICEPERIOD_NAME = QName.createQName(contractModelURI, "servicePeriodName");
	public static QName QN_SERVICEPERIOD_DESCRIPTION = QName.createQName(contractModelURI, "servicePeriodDescription");
	public static QName QN_SERVICE_TYPE_CODE = QName.createQName(contractModelURI, "serviceTypeCode");
	public static QName QN_SERVICE_PERIOD_TYPE = QName.createQName(contractModelURI, "servicePeriodType");
	public static QName QN_SERVICE_START = QName.createQName(contractModelURI, "serviceStart");
	public static QName QN_SERVICE_END = QName.createQName(contractModelURI, "serviceEnd");
	
	public String servicePeriodId;
	public String servicePeriodName;
	public String servicePeriodDescription;
	public String serviceTypeCode;
	public String servicePeriodType;
	public Date   serviceStart;
	public Date   serviceEnd;

}
