package com.nvp.alfresco.docx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.alfresco.model.ContentModel;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.docx4j.model.datastorage.BindingHandler;
import org.docx4j.model.datastorage.CustomXmlDataStorage;
import org.docx4j.model.datastorage.CustomXmlDataStoragePartSelector;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.CustomXmlDataStoragePart;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
	 * A way to write to the log file
	 * 
	 */
	private static Log logger = LogFactory.getLog(WordRenderer.class);
	
	/**
	 * 
	 * MIME type for DOCX content
	 * 
	 */
	private final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	
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
	public void mergeProperties(Map<QName, Serializable> properties) throws WordRendererException {
		
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
		
		/** update the nodes in the custom.xml document which match the properties provided */
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
		

		// ContentService contentService = registry.getContentService();
		// ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
		
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
        
    	FileFolderService fileService =registry.getFileFolderService();
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
		
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);
		is.close();
		
		this.logger.debug("Created the WordML package for node [" + nodeRef + "]");
		
		return wordMLPackage;
	}

    /**
     * 
     * Obtain the 'CustomXML' data file from the Word package
     * @throws Docx4JException 
     * 
     */
    private org.w3c.dom.Document getCustomXMLStorage(WordprocessingMLPackage wordMLPackage) throws Docx4JException {
    	
    		CustomXmlDataStoragePart customXmlDataStoragePart 
    			= CustomXmlDataStoragePartSelector.getCustomXmlDataStoragePart(wordMLPackage);		
		
    		if (customXmlDataStoragePart==null) {
    			throw new Docx4JException("Couldn't find CustomXmlDataStoragePart in WordMLPackage");}
		
    		CustomXmlDataStorage xmlStorage = customXmlDataStoragePart.getData();
    		org.w3c.dom.Document xmlDoc = xmlStorage.getDocument();
    		return xmlDoc;
    		
    		// return xmlDoc.getDocumentElement().getNamespaceURI().toString();
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
		
		ContentWriter writer2 = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);

		logger.debug("Updated node with new content");
	}
	
	/**
	 * 
	 * Takes the properties of a node and merges them into the Word document contained in the node
	 * 
	 * @param documentNode
	 * @param serviceRegistry
	 */
	public static void mergePropertiesToDocument (NodeRef documentNode, ServiceRegistry serviceRegistry) {
		
		try {
			 
			 Map<QName, Serializable> wordProperties = new HashMap<QName, Serializable>();
			 NodeService nodeService = serviceRegistry.getNodeService();
			 wordProperties = nodeService.getProperties(documentNode);
			 
			 WordPropertiesManager wordPropertiesManager = new WordPropertiesManager();
			 wordPropertiesManager.setServiceRegistry(serviceRegistry);
		     wordPropertiesManager.setWordNodeRef(documentNode);
		     wordPropertiesManager.mergeProperties(wordProperties);
		     wordPropertiesManager.writeToNodeContent(documentNode);
		 
		 } catch (Exception e) {
			 
			 System.out.println("ERROR Unable to merge properties into document, " + e.getMessage());
			 
		 }

	}

}
