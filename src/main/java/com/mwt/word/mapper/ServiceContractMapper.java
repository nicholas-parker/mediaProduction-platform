package com.mwt.word.mapper;
 
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.namespace.QName;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mwt.alfresco.model.PersonModel;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.contract.model.RegisteredOrgModel;
import com.mwt.contract.model.INdividu;
import com.mwt.production.MediaProductionOrganisationModel;
import com.mwt.roles.ProductionRoleModel;
import com.nvp.alfresco.docx.Mapper;

/**
 * This class maps between 
 * 
 * 		word schema 'http://mwt.com/model/word/serviceContract/1.0'
 *      and Alfresco properties
 *      
 * @author nickp
 *
 */
public class ServiceContractMapper extends Mapper {

	/**
	 * 
	 * namespace and properties for Word customCMLcontent model
	 *  - serviceContractDocument
	 * 
	 */
	public static String TARGET_NAMESPACE = "http://mwt.com/model/word/serviceContract/1.0";
	public static String QUALIFIED_ROOT_ELEMENT = "wd:contract";
	public static String LOCAL_ROLE_REF = "roleRef";
	public static String LOCAL_ROLE_NAME = "roleName";
	public static String LOCAL_ROLE_DESCRIPTION = "roleDescription";
	public static String LOCAL_START_DATE = "startDate";
	public static String LOCAL_END_DATE = "endDate";
	public static String LOCAL_PAYE_STATUS = "PAYEstatus";
	public static String LOCAL_CONTRACT_VALUE = "contractValue";
	public static String LOCAL_CONTRACT_VALUE_CURRENCY = "contractValueCurrency";
	public static String LOCAL_CONTRACT_PERIOD_SPECIFIER = "periodSpecifier";
	public static String LOCAL_OVERTIME_PAYABLE = "overtimePayable";
	public static String LOCAL_OVERTIME_RATE = "overtimeRate";
	public static String LOCAL_NOTICE_PERIOD_SPECIFIER = "noticePeriodSpecifier";
	public static String LOCAL_NOTICE_PERIOD_DURATION = "noticePeriodDuration";
	public static String LOCAL_LOCATION = "location";
	public static String LOCAL_PROVIDER_NAME = "serviceProviderName";
	public static String LOCAL_PROVIDER_ORGANISATION = "serviceProviderOrganisation";
	public static String LOCAL_PROVIDER_ADDRESS = "serviceProviderAddress";
	public static String LOCAL_PROVIDER_REG_NUMBER = "serviceProviderCoNumber";
	public static String LOCAL_PROVIDER_VAT_NUMBER = "serviceProviderVATNum";
	public static String LOCAL_CONTRACT_DATE = "contractDate";
	public static String LOCAL_CLIENT_NAME = "clientName";
	public static String LOCAL_CLIENT_ADDRESS = "clientAddress";
	public static String LOCAL_HS_NOTIFICATIONS = "hsProviderNotified";
	public static String LOCAL_HS_MITIGATIONS = "hsClientMitigated";

	
	/**
	 * Initialise the mappings
	 */
	public void initialiseMap() {
		
		if(mapping != null) { return; }
		
		mapping = new HashMap<javax.xml.namespace.QName, List<org.alfresco.service.namespace.QName>>();
		
		javax.xml.namespace.QName xmlName;
		List<org.alfresco.service.namespace.QName> alfNames;
		
		/**
		 * contract code
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_ROLE_REF);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(ContractDocumentModel.QN_CONTRACT_CODE);
        mapping.put(xmlName, alfNames);
		
		/**
		 * role name
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_ROLE_NAME);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(INdividu.QN_SERVICE_NAME);
        mapping.put(xmlName, alfNames);
        
        /**
         * role description
         */
        xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_ROLE_DESCRIPTION);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(INdividu.QN_SERVICE_DESCRIPTION);
        mapping.put(xmlName, alfNames);
        
		/**
		 * startDate
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_START_DATE);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(INdividu.QN_SERVICE_START);
		mapping.put(xmlName, alfNames);

		/**
		 * endDate
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_END_DATE);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(INdividu.QN_SERVICE_END);
		mapping.put(xmlName, alfNames);

		/**
		 * PAYEstatus
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_PAYE_STATUS);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(ContractDocumentModel.QN_PAYE_STATUS);
		mapping.put(xmlName, alfNames);

		/**
		 * contractValue
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_CONTRACT_VALUE);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(ContractDocumentModel.QN_CONTRACT_VALUE);
		mapping.put(xmlName, alfNames);

		/**
		 * contractValueCurrency
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_CONTRACT_VALUE_CURRENCY);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(ContractDocumentModel.QN_CONTRACT_VALUE_CURRENCY);
		mapping.put(xmlName, alfNames);

		/**
		 * period specifier
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_CONTRACT_PERIOD_SPECIFIER);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(ContractDocumentModel.QN_CONTRACT_PAYMENT_PERIOD_SPECIFIER);
		mapping.put(xmlName, alfNames);
		
		/**
		 * period duration - NOT MAPPED
		 */
		
		/**
		 * overtime payable
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_OVERTIME_PAYABLE);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(ContractDocumentModel.QN_OVERTIME_PAYABLE);
		mapping.put(xmlName, alfNames);
		
		/**
		 * overtime rate 
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_OVERTIME_RATE);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(ContractDocumentModel.QN_OVERTIME_RATE);
		mapping.put(xmlName, alfNames);
		
		/**
		 * notice period specifier NOT MAPPED, assumed days
		 */
		
		/**
		 * notice period duration
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_NOTICE_PERIOD_DURATION);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(ContractDocumentModel.QN_CONTACT_NOTICE_PERIOD);
		mapping.put(xmlName, alfNames);
		
		/**
		 * location service to be delivered at
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_LOCATION);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(ContractDocumentModel.QN_LOCATION);
		mapping.put(xmlName, alfNames);
		
		/**
		 * providerName
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_PROVIDER_NAME);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(PersonModel.QN_FIRST_NAME);
		alfNames.add(PersonModel.QN_MIDDLE_NAME);
		alfNames.add(PersonModel.QN_LAST_NAME);
		mapping.put(xmlName, alfNames);

		/**
		 * providerOrganisation
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_PROVIDER_ORGANISATION);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(PersonModel.QN_ORGANISATION);
		mapping.put(xmlName, alfNames);
		
		/**
		 * providerAddress
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_PROVIDER_ADDRESS);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(PersonModel.QN_COMPANYADDRESS1);
		alfNames.add(PersonModel.QN_COMPANYADDRESS2);
		alfNames.add(PersonModel.QN_COMPANYADDRESS3);
		alfNames.add(PersonModel.QN_COMPANYPOSTCODE);
		mapping.put(xmlName, alfNames);

		/**
		 * providerRegNum
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_PROVIDER_REG_NUMBER);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(RegisteredOrgModel.QN_REGISTERED_NUMBER);
		mapping.put(xmlName, alfNames);
		
		/**
		 * providerVATNumber
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_PROVIDER_VAT_NUMBER);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(RegisteredOrgModel.QN_TAX_NUMBER);
		mapping.put(xmlName, alfNames);

		/**
		 * contract date
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_CONTRACT_DATE);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(ContractDocumentModel.QN_CONTRACT_DATE);
		mapping.put(xmlName, alfNames);
		
		/**
		 * client name
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_CLIENT_NAME);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(MediaProductionOrganisationModel.QN_PRODUCTION_ORGANISATION_NAME);
		mapping.put(xmlName, alfNames);

		/**
		 * client address
		 */
		xmlName = new javax.xml.namespace.QName(TARGET_NAMESPACE, LOCAL_CLIENT_ADDRESS);
		alfNames =  new ArrayList<org.alfresco.service.namespace.QName>();
		alfNames.add(MediaProductionOrganisationModel.QN_PRODUCTION_ADDR1);
		alfNames.add(MediaProductionOrganisationModel.QN_PRODUCTION_ADDR2);
		alfNames.add(MediaProductionOrganisationModel.QN_PRODUCTION_ADDR3);
		alfNames.add(MediaProductionOrganisationModel.QN_PRODUCTION_PO_CODE);
		mapping.put(xmlName, alfNames);
		
		/**
		 * HS Notifications - NOT MAPPED
		 */
		
		/**
		 * HS Mitigations - NOT MAPPED
		 */

	}
	
	
	/**
	 * 
	 * @return the HashMap which gives the mappings between XML elements and Alfresco properties
	 * 
	 */
	public HashMap<javax.xml.namespace.QName, List<org.alfresco.service.namespace.QName>> getMap() {
		
		if(mapping == null) {
			initialiseMap();
		}
		
		return mapping;
		
	}
	
	/**
	 * Creates a new XML file which can be applied to the document
	 * 
	 * @param props a Map of the document properties
	 * @return a string representation of the XML file
	 * @throws WordMapperException
	 */
	@Override
	public String toXML(Map<QName, Serializable> props) throws WordMapperException {

		try {

			DOMImplementation domImpl = getDOMImpl();
			Document doc = domImpl.createDocument(TARGET_NAMESPACE, QUALIFIED_ROOT_ELEMENT, null);

			/**
			 * role name
			 */
			Element roleName = doc.createElement(LOCAL_ROLE_NAME);
			roleName.appendChild(doc.createTextNode(
					getStringValue(props, ProductionRoleModel.QN_ROLE_NAME)));
			doc.appendChild(roleName);

			/**
			 * startDate
			 */
			Element startDate = doc.createElement(LOCAL_START_DATE);
			startDate.appendChild(doc.createTextNode(
					getDateValue(props, ProductionRoleModel.QN_START_DATE)));
			doc.appendChild(startDate);

			/**
			 * endDate
			 */
			Element endDate = doc.createElement(LOCAL_END_DATE);
			endDate.appendChild(doc.createTextNode(
					getDateValue(props, ProductionRoleModel.QN_END_DATE )));
			doc.appendChild(endDate);

			/**
			 * PAYEstatus
			 */
			Element PAYEstatus = doc.createElement(LOCAL_PAYE_STATUS);
			PAYEstatus.appendChild(doc.createTextNode(
					getStringValue(props, ProductionRoleModel.QN_PAYE_STATUS)));
			doc.appendChild(endDate);

			/**
			 * contractValue
			 */
			Element contractValue = doc.createElement(LOCAL_CONTRACT_VALUE);
			contractValue.appendChild(doc.createTextNode(
					getStringValue(props, ContractDocumentModel.QN_CONTRACT_VALUE)));
			doc.appendChild(contractValue);

			/**
			 * contractValueCurrency
			 */
			Element contractValueCurrency = doc.createElement(LOCAL_CONTRACT_VALUE_CURRENCY);
			contractValueCurrency.appendChild(doc.createTextNode(
					getStringValue(props, ContractDocumentModel.QN_CONTRACT_VALUE_CURRENCY)));
			doc.appendChild(contractValueCurrency);

			/**
			 * location service to be delivered at
			 * TO DO - not yet in source model
			 */
			Element location = doc.createElement(LOCAL_LOCATION);
			location.appendChild(doc.createTextNode(
					"38 West Wilderbeast Road, London WC1 8TY"));
			doc.appendChild(location);

			/**
			 * providerName
			 */
			StringBuilder name = new StringBuilder(); 
			name.append(getStringValue(props, PersonModel.QN_FIRST_NAME));
			name.append(" ");
			name.append(getStringValue(props, PersonModel.QN_MIDDLE_NAME));
			name.append(" ");
			name.append(getStringValue(props, PersonModel.QN_LAST_NAME));

			Element providerName = doc.createElement(LOCAL_PROVIDER_NAME);
			providerName.appendChild(doc.createTextNode(name.toString()));
			doc.appendChild(providerName);

			/**
			 * providerOrganisation
			 */
			Element providerOrganisation = doc.createElement(LOCAL_PROVIDER_ORGANISATION);
			providerOrganisation.appendChild(doc.createTextNode(
					getStringValue(props, PersonModel.QN_ORGANISATION).toString()));
			doc.appendChild(providerOrganisation);

			/**
			 * providerAddress
			 */
			StringBuilder address = new StringBuilder(); 
			address.append(getStringValue(props, PersonModel.QN_COMPANYADDRESS1));
			address.append(" ");
			address.append(getStringValue(props, PersonModel.QN_COMPANYADDRESS2));
			address.append(" ");
			address.append(getStringValue(props, PersonModel.QN_COMPANYADDRESS3));
			address.append(" ");
			address.append(getStringValue(props, PersonModel.QN_COMPANYPOSTCODE));

			Element providerAddress = doc.createElement(LOCAL_PROVIDER_ADDRESS);
			providerAddress.appendChild(doc.createTextNode(address.toString()));
			doc.appendChild(providerAddress);

			/**
			 * providerRegNum
			 */
			Element providerRegNum = doc.createElement(LOCAL_PROVIDER_REG_NUMBER);
			providerRegNum.appendChild(doc.createTextNode(
					getStringValue(props, RegisteredOrgModel.QN_REGISTERED_NUMBER)));
			doc.appendChild(providerRegNum);

			/**
			 * providerVATNumber
			 */
			Element providerVATNum = doc.createElement(LOCAL_PROVIDER_VAT_NUMBER);
			providerVATNum.appendChild(doc.createTextNode(
					getStringValue(props, RegisteredOrgModel.QN_TAX_NUMBER).toString()));
			doc.appendChild(providerRegNum);

			/**
			 * obtain a string
			 */
			return docAsString(doc);

		} catch (Exception e) {

			e.printStackTrace();		
			throw new WordMapperException("Error creating a CustomXMLComponent XML document by mapper", e);
			
		}
	}

	/**
	 * Updates an existing CustomXMLPart document with the properties provided.
	 * Properties are mapped into the XML document using the mapper list
	 * 
	 * @param props
	 * @param xml
	 * @return
	 */
    public Document updateXML(Map<QName, Serializable> props, Document xml) {
    	return null;
    }

	@Override
	public Map<QName, Serializable> fromXML(String xmlString) {
		// TODO Auto-generated method stub
		return null;
	}
}
