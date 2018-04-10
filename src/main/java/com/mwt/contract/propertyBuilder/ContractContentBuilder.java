package com.mwt.contract.propertyBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.contract.model.ServicePeriodModel;
import com.mwt.production.MediaProductionModel;
import com.nvp.alfresco.docx.WordRenderer;

public class ContractContentBuilder {

	/**
	 * 
	 * Factory to build the custom xml parts which are injected into documents
	 * 
	 */
	private static DocumentBuilderFactory dbf = DocumentBuilderFactory
            .newInstance();
	/**
	 * 
	 * A way to write to the log file
	 * 
	 */
	private static Log logger = LogFactory.getLog(ContractContentBuilder.class);

	/**
	 * 
	 * the instance of the document we are building
	 * 
	 */
	private Document document = null;
	
	/**
	 * 
	 * @param properties
	 * @return an XML document of properties.
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
    public void propertiesToCustomContent(Map<QName, Serializable> properties) throws ParserConfigurationException, TransformerException {
    	
    	 String prefix = "c";
    	 String rootNodeName = "contract";
    	 DocumentBuilder db = dbf.newDocumentBuilder();
         DOMImplementation domImpl = db.getDOMImplementation();
         this.document = domImpl.createDocument(ContractDocumentModel.contractModelURI, prefix + ":" + rootNodeName, null);
         this.document.getDocumentElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:p", MediaProductionModel.productionModelURI);
         
         List<String> validNamespaces = new ArrayList<String>();
         validNamespaces.add(ContractDocumentModel.contractModelURI);
         validNamespaces.add(MediaProductionModel.productionModelURI);
         
         for (Map.Entry<QName, Serializable> entry : properties.entrySet()) {
        	 
        	 if(validNamespaces.contains(entry.getKey().getNamespaceURI()))
        	 {
        		 String elementPrefix = this.document.lookupPrefix(entry.getKey().getNamespaceURI());
        		 String localName = null;
        		 try {
        		   localName = entry.getKey().getLocalName();
        		   Element element = this.document.createElement( elementPrefix + ":" + localName);
        		   if(null == entry.getValue()) {
					   element.setTextContent(null); 
					 } else {
					   element.setTextContent(entry.getValue().toString());	 
					 }
        		   this.document.getDocumentElement().appendChild(element);
        		 } catch (DOMException e) {
 					System.out.println("ERROR: unable to add [" + prefix + "]:[" + localName + "] into customXML in word document");
 					e.printStackTrace();
 				}
        	 } else {
        		 
        		 System.out.println("INFO: Creating document content, ignoring {" +  entry.getKey().getNamespaceURI() + "} " + entry.getKey().getLocalName() + "\n");
        	 }
       	}
    }
    
    /**
     * 
     * add service periods into the Document, add properties first to create the document
     * 
     * @param periods
     */
    public void addServicePeriod(String servicePeriodId,
    		                     String servicePeriodName,
    		                     String servicePeriodDescription,
    		                     String serviceTypeCode,
    		                     String servicePeriodType,
    		                     String serviceStart,
    		                     String serviceEnd) {
    	
    	
    	String servicePeriodPrefix = "c";
    	String servicePeriodLN = "servicePeriod";
    	String servicePeriodIdLN = "servicePeriodId";
    	String servicePeriodNameLN = "servicePeriodName";
    	String servicePeriodDescriptionLN = "serviePeriodDescription";
    	String serviceTypeCodeLN = "servicePeriodTypeCode";
    	String servicePeriodTypeLN = "servicePeriodType";
    	String serviceStartLN = "serviceStart";
    	String serviceEndLN = "serviceEnd";
    	
    	
    	if(this.document == null ) {
    		return;
    	}
    	
    	Element periodRoot = this.document.createElement( servicePeriodPrefix + ":" + servicePeriodLN);
    	
    	Element ePeriodId = this.document.createElement( servicePeriodPrefix + ":" + servicePeriodIdLN);
    	ePeriodId.setTextContent(servicePeriodId);
    	periodRoot.appendChild(ePeriodId);
    	
    	Element ePeriodName = this.document.createElement( servicePeriodPrefix + ":" + servicePeriodNameLN);
    	ePeriodName.setTextContent(servicePeriodName);
    	periodRoot.appendChild(ePeriodName);
    	
    	Element ePeriodDesc = this.document.createElement( servicePeriodPrefix + ":" + servicePeriodDescriptionLN);
    	ePeriodDesc.setTextContent(servicePeriodId);
    	periodRoot.appendChild(ePeriodDesc);
    	
    	Element eServiceTypeCode = this.document.createElement( servicePeriodPrefix + ":" + serviceTypeCodeLN);
    	eServiceTypeCode.setTextContent(serviceTypeCode);
    	periodRoot.appendChild(eServiceTypeCode);
    	
    	Element eServicePeriodType = this.document.createElement( servicePeriodPrefix + ":" + servicePeriodTypeLN);
    	eServicePeriodType.setTextContent(servicePeriodType);
    	periodRoot.appendChild(eServicePeriodType);
    	
    	Element eServiceStart = this.document.createElement( servicePeriodPrefix + ":" + serviceStartLN);
    	eServiceStart.setTextContent(serviceStart);
    	periodRoot.appendChild(eServiceStart);
    	
    	Element eServiceEnd = this.document.createElement( servicePeriodPrefix + ":" + serviceEndLN);
    	eServiceEnd.setTextContent(serviceEnd);
    	periodRoot.appendChild(eServiceEnd);
    	
    	this.document.getDocumentElement().appendChild(periodRoot);
    	
    }
    
    /**
     * 
     * Access to the XML document 
     * @return
     */
    public Document getDocument() {
    	
    	return this.document;
    }
    
    
}
