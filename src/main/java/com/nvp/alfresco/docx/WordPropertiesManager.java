package com.nvp.alfresco.docx;

import java.io.ByteArrayInputStream; 
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.production.MediaProductionModel;

import org.w3c.dom.Document;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.docx4j.Docx4J;
import org.docx4j.model.datastorage.BindingHandler;
import org.docx4j.model.datastorage.CustomXmlDataStorage;
import org.docx4j.model.datastorage.CustomXmlDataStoragePartSelector;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.CustomXmlDataStoragePart;
import org.docx4j.openpackaging.parts.CustomXmlPart;
import org.docx4j.services.client.ConversionException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class which manages the read and update of custom properties in an MSWord document
 * 
 * @author nick
 *
 */
public class WordPropertiesManager {

	/**
	 * 
	 * a reference to the global Alfresco ServiceRegistry
	 * 
	 */
	private ServiceRegistry registry;
	
	/**
	 * 
	 * a reference to the Alfresco node which contains a word document we want to manage the properties in
	 * 
	 */
	private NodeRef targetNode = null;
	
	/**
	 * 
	 * A Word document instance which we can set the properties in, loaded from targetNode
	 * 
	 */
	private WordprocessingMLPackage wordMLPackage = null;
	
	/**
	 * 
	 * A DOM implementation of the Word documents custom.xml file
	 * 
	 */
	private org.w3c.dom.Document custom_xml;
	
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
	private static Log logger = LogFactory.getLog(WordRenderer.class);
	
	/**
	 * 
	 * MIME typesor DOCX & PDF content
	 * 
	 */
	private final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	private final String PDF_MIME_TYPE = "application/pdf";
	
	/**
	 * 
	 * @param nodeRef
	 */
	public void setWordNodeRef(NodeRef nodeRef)  {
		
		this.targetNode = nodeRef;
	}
	
	/**
	 * 
	 * provides the instance with a reference to the service registry
	 * 
	 */
	public void setServiceRegistry(ServiceRegistry registry) {
		
		this.registry = registry;
		
	}
	
	/**
	 * 
	 * Takes a list of Alfresco properties and merges the values into the custom XML data file in the word document.
	 * The XML file must have a text node which matches the namespace and name of the Alfresco property
	 * 
	 * If document is not already open then it is opened.
	 * 
	 * @param properties
	 * @throws WordRendererException
	 */
	public void mergeProperties(Document customXML) throws WordRendererException {
		
		/**
		 *  
		 * must have a reference to the node containing word doc, should have been set before calling this method 
		 * must have some properties
		 * 
		 */
		if(null == targetNode) {
			throw new WordRendererException("Error merging Word custom properties, target document not set, use setWordNodeRef()");
		}
		
		if(null == customXML) {
			throw new WordRendererException("Error merging Word custom properties, map of supplied properties is null");
		}
		
		/**
		 * 
		 * create an XML document stream of the properties, this will be merged into the document by docx4j library
		 * 
		 */
		ByteArrayOutputStream outputStream = null;
		try {
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        DOMSource source = new DOMSource(customXML);
	        outputStream = new ByteArrayOutputStream();
	        StreamResult result = new StreamResult(outputStream);
	        transformer.transform(source, result);
            outputStream.close();
            
	        // Output to console for testing
	        StreamResult consoleResult = new StreamResult(System.out);
	        transformer.transform(source, consoleResult);
	        
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
		    ioe.printStackTrace();
		}
		
		
		
		/** open the document into a Word processing package */
		try {
		  this.wordMLPackage = openWordDoc(this.targetNode);
		} catch (Exception e) {
			throw new WordRendererException("Error reading word package", e);
		}
		
	
		/** replace existing properties with new ones in the xml stream and bind to content
		 * 
		 * FLAG_NONE means that all the steps of the binding will be done,
		 * otherwise you could pass a combination of the following flags:
		 * FLAG_BIND_INSERT_XML: inject the passed XML into the document
		 * FLAG_BIND_BIND_XML: bind the document and the xml (including any OpenDope handling)
		 * FLAG_BIND_REMOVE_SDT: remove the content controls from the document (only the content remains)
		 * FLAG_BIND_REMOVE_XML: remove the custom xml parts from the document 
		 *			
		 * Docx4J.bind(wordMLPackage, xmlStream, Docx4J.FLAG_NONE);
		 * If a document doesn't include the Opendope definitions, eg. the XPathPart,
		 * then the only thing you can do is insert the xml
		 * the example document binding-simple.docx doesn't have an XPathPart....
		 * 
		 */
		try {
			InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			Docx4J.bind(wordMLPackage, inputStream, Docx4J.FLAG_BIND_INSERT_XML | Docx4J.FLAG_BIND_BIND_XML);
		} catch (Docx4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception ee) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: unexpected error when trying to bind XML content into document");
			ee.printStackTrace();
		}
		// System.out.println("Bound content to document");
	}
		
		/** 
		 * @throws WordRendererException 
		 * @deprecated - until we can work out a reliable way to get custom.xml, until then use the merge properties method
		 * 
		 * update the nodes in the custom.xml document which match the properties provided 
		 * 
		 */
	    public void updateCustomXml(Map<QName, Serializable> properties) throws WordRendererException {
	    
		Set<Entry<QName,Serializable>> entries = properties.entrySet();
	    for(Entry<QName, Serializable> entry : entries) {
		
	    	String value = null;
			if(null != entry.getValue()) {
			  value = entry.getValue().toString();
			} else {
			  value = "";
			}
			QName qname = entry.getKey();
			NodeList targetNodes = this.custom_xml.getElementsByTagNameNS(qname.getNamespaceURI(), qname.getLocalName());

			if(targetNodes.getLength() > 0) {
				targetNodes.item(0).setTextContent(value);
			}
	    }
		
		
		try {
		
			BindingHandler bh = new BindingHandler(wordMLPackage);
			bh.applyBindings(wordMLPackage.getMainDocumentPart());

		} catch ( Exception e) {
			
			System.out.print(e.getMessage());
			throw new WordRendererException("Error binding new values back to document", e);
		   	
		}
		
	}
	
	public Map<QName, Serializable> getAllProperties() throws WordRendererException {
		
		/** must have a reference to the node containing word doc */
		if(null == targetNode) {
			throw new WordRendererException("Error merging Word custom properties, target document not set, use setWordNodeRef()");
		}
		
		/** open the document into a Word processing package */
		try {
		  this.wordMLPackage = openWordDoc(this.targetNode);
		} catch (Exception e) {
			throw new WordRendererException("Error reading word package", e);
		}
		
		/** extract the custom.xml file from the Word document */
		try {
		  this.custom_xml = getCustomXMLStorage(this.wordMLPackage);
		} catch (Exception e) {
			throw new WordRendererException("Error extracting custom.xml from the Word document", e);
		}
		
	    /** flatten XML document into Map<QName, Serializable> */
		return null;
	    
	}
	
	public Map<QName, Serializable> getProperties(Map<QName, Serializable> targetProperties) {
		return null;
	}
	
	
	/**
	 * 
	 * Using the given Alfresco NodeRef create a Word package which contains the binary content
	 * from the node.  The content must be a Word document.
	 * 
	 * @param nodeRef
	 * @return
	 * @throws Docx4JException
	 * @throws IOException
	 * @throws WordRendererException 
	 * 
	 */
    private WordprocessingMLPackage openWordDoc(NodeRef nodeRef) throws Docx4JException, IOException, WordRendererException {
		
    	ContentData contentData = null;
    	Map<QName, Serializable> props = this.registry.getNodeService().getProperties(nodeRef); 
        Serializable propValue = this.registry.getNodeService().getProperty(nodeRef, ContentModel.PROP_CONTENT);
        if (propValue instanceof Collection)
        {
            Collection<Serializable> colPropValue = (Collection<Serializable>)propValue;
            if (colPropValue.size() > 0)
            {
                propValue = colPropValue.iterator().next();
            }
        }

        if (propValue instanceof ContentData)
        {
            contentData = (ContentData)propValue;
        }

        if (contentData == null)
        {
            PropertyDefinition contentPropDef = this.registry.getDictionaryService().getProperty(ContentModel.PROP_CONTENT);
            
            // if no value or a value other content, and a property definition has been provided, ensure that it's CONTENT or ANY            
            if (contentPropDef != null && 
                (!(contentPropDef.getDataType().getName().equals(DataTypeDefinition.CONTENT) ||
                   contentPropDef.getDataType().getName().equals(DataTypeDefinition.ANY))))
            {
                throw new InvalidTypeException("The node property must be of type content: \n" +
                        "   node: " + nodeRef + "\n" +
                        "   property name: " + ContentModel.PROP_CONTENT + "\n" +
                        "   property type: " + ((contentPropDef == null) ? "unknown" : contentPropDef.getDataType()),
                        ContentModel.PROP_CONTENT);
            }
        }

        if (contentData == null || contentData.getContentUrl() == null)
        {
            // there is no URL - the interface specifies that this is not an error condition
            return null;                                
        }
        String contentUrl = contentData.getContentUrl();
        
        // END OF IT
        
    	FileFolderService fileService = registry.getFileFolderService();
    	ContentReader reader = fileService.getReader(nodeRef);
    	
		if( null == reader ) {

			this.logger.error("Unable to obtain ContentReader for node " + nodeRef.toString());
			throw new WordRendererException("Unable to obtain ContentReader for node " + nodeRef.toString());
		
		}
		
		InputStream is = reader.getContentInputStream();
		if( null == is ) {

			this.logger.error("Unable to get ContentInputStream for " + nodeRef.toString());
			throw new WordRendererException("Unable to get ContentInputStream for node " + nodeRef.toString());
			
		}
		
		WordprocessingMLPackage wordMLPackage = Docx4J.load(is);
		is.close();
		
		this.logger.debug("Created the WordML package for node [" + nodeRef + "]");
		
		return wordMLPackage;
	}

    /**
     * @deprecated
     * 
     * Obtain the 'CustomXML' data file from the Word package
     * @throws Docx4JException 
     * 
     * DEPRECATED - this approach is no longer used as extracting the XML part was unreliable when there were multiple parts
     * 
     */
    private org.w3c.dom.Document getCustomXMLStorage(WordprocessingMLPackage wordMLPackage) throws Docx4JException {
    	
    	    HashMap<String, CustomXmlPart> partMap = wordMLPackage.getCustomXmlDataStorageParts();
    	    if(null == partMap  || partMap.values().size() == 0)  {
    			throw new Docx4JException("Couldn't find CustomXmlDataStoragePart in WordMLPackage");}
    	    
    	    CustomXmlDataStoragePart[] partArray = partMap.values().toArray(new CustomXmlDataStoragePart[] {});
    	    
    		CustomXmlDataStorage xmlStorage = partArray[0].getData();
    		org.w3c.dom.Document xmlDoc = xmlStorage.getDocument();
    		return xmlDoc;
    }
    
	/**
	 * 
	 * @param nodeRef
	 * @throws Docx4JException
	 * @throws IOException
	 * 
	 */
	public void writeToNodeContent(NodeRef nodeRef) throws Docx4JException, IOException {
		
		ContentService contentService = registry.getContentService();
		ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
		// writer.setMimetype(DOCX_MIME_TYPE);

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		this.wordMLPackage.save(outStream);
		InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
		writer.putContent(inStream);
		outStream.close();
		inStream.close();
		
		logger.debug("Updated node with new content");
	}
	
	public void writeToNodeContentAsPDF(NodeRef nodeRef) {
	
		ContentService contentService = registry.getContentService();
		ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
		writer.setMimetype(PDF_MIME_TYPE);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		
		
		try {
			
			Boolean result = this.registry.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>(){
			    
				public Boolean execute() throws Throwable {

					/** convert to PDF and write to node content */
					
					Docx4J.toPDF(wordMLPackage, outStream);
					InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());			
					writer.putContent(inStream);
					outStream.close();
					inStream.close();
					
					/** change the document filename */
					NodeService nodeService = registry.getNodeService();
					String fileName = nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString();
					String newName;
					if(fileName.contains(".")) {
						String[] parts = fileName.split(".");
						newName = parts[0] + ".pdf";
					} else {
						newName = fileName + ".pdf";
					}
					nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, newName);
					System.out.println("Saved document as PDF");

					return true;
			    }   
			});
			
			
		} catch (Throwable e) {
			
			e.printStackTrace();
			IOUtils.closeQuietly(outStream);
			if (e.getCause()!=null
					&& e.getCause() instanceof ConversionException) {
				
				ConversionException ce = (ConversionException)e.getCause();
				ce.printStackTrace();
			}
			
		} 
		
		return;

	}
	
	/**
	 * 
	 * @param properties
	 * @return an XML document of properties.
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
    private org.w3c.dom.Document propertiesToCustomContent(Map<QName, Serializable> properties) throws ParserConfigurationException, TransformerException {
    	
    	 String prefix = "c";
    	 String rootNodeName = "contract";
    	 DocumentBuilder db = dbf.newDocumentBuilder();
         DOMImplementation domImpl = db.getDOMImplementation();
         Document document = domImpl.createDocument(ContractDocumentModel.contractModelURI, prefix + ":" + rootNodeName, null);
         document.getDocumentElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:p", MediaProductionModel.productionModelURI);
         
         
         
         List<String> validNamespaces = new ArrayList<String>();
         validNamespaces.add(ContractDocumentModel.contractModelURI);
         validNamespaces.add(MediaProductionModel.productionModelURI);
         
         for (Map.Entry<QName, Serializable> entry : properties.entrySet()) {
        	 
        	 if(validNamespaces.contains(entry.getKey().getNamespaceURI()))
        	 {
        		 String elementPrefix = document.lookupPrefix(entry.getKey().getNamespaceURI());
        		 String localName = null;
        		 try {
        		   localName = entry.getKey().getLocalName();
        		   Element element = document.createElement( elementPrefix + ":" + localName);
        		   if(null == entry.getValue()) {
					   element.setTextContent(null); 
					 } else {
					   element.setTextContent(entry.getValue().toString());	 
					 }
        		   document.getDocumentElement().appendChild(element);
        		 } catch (DOMException e) {
 					System.out.println("ERROR: unable to add [" + prefix + "]:[" + localName + "] into customXML in word document");
 					e.printStackTrace();
 				}
        	 } else {
        		 
        		 System.out.println("INFO: Creating document content, ignoring {" +  entry.getKey().getNamespaceURI() + "} " + entry.getKey().getLocalName() + "\n");
        	 }
       	}
         
       return document;

    }
}
