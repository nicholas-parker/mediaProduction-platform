package com.mwt.contract.model;

import org.alfresco.service.namespace.QName;

public class CrewTravelExpensesModel {

	// namespaces, types
	public static String contractAspectName = "crewTravelExpensesModel";
	public static String prefix = "contract";
	public static String contractModelURI = "http://mwt.com/model/contract/1.0";

	public static String PROP_CONTRACT_TRAVEL_EXPENSES_AVAILABLE = "contractTravelExpensesAvailable";
	public static String PROP_CONTRACT_TRAVEL_EXPENSES_MIN_DISTANCE = "contractTravelExpensesMinDistance";
	public static String PROP_CONTRACT_TRAVEL_MILEAGE_CLAIM_PENCE = "contractTravelMileageClaimPence";
	
	public static QName QN_CONTRACT_TRAVEL_EXPENSES_AVAILABLE = QName.createQName(contractModelURI, PROP_CONTRACT_TRAVEL_EXPENSES_AVAILABLE);
	public static QName QN_CONTRACT_TRAVEL_EXPENSES_MIN_DISTANCE = QName.createQName(contractModelURI, PROP_CONTRACT_TRAVEL_EXPENSES_MIN_DISTANCE);
	public static QName QN_CONTRACT_TRAVEL_MILEAGE_CLAIM_PENCE = QName.createQName(contractModelURI, PROP_CONTRACT_TRAVEL_MILEAGE_CLAIM_PENCE);
	
}
