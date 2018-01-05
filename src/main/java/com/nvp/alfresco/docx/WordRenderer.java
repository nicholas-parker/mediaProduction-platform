package com.nvp.alfresco.docx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.docx4j.model.datastorage.BindingHandler;
import org.docx4j.model.datastorage.CustomXmlDataStorage;
import org.docx4j.model.datastorage.CustomXmlDataStoragePartSelector;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.CustomXmlDataStoragePart;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mwt.word.mapper.WordMapperException;
 
public class WordRenderer {

	private WordprocessingMLPackage wordMLPackage = null;
	private NodeRef nodeRef = null;
	    
    private ServiceRegistry registry;
    
    private final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    
    private static Log logger = LogFactory.getLog(WordRenderer.class);
    
    
    // for Spring injection 
    public void setServiceRegistry(ServiceRegistry registry) {
        this.registry = registry;
    }

    public WordRenderer() {
    	
    }

    public WordRenderer(NodeRef nodeRef, ServiceRegistry registry) {
    	
    	try {
    		this.registry = registry;
    		this.nodeRef = nodeRef;
    		openWordDoc(nodeRef);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public String getCustomXMLNamespace() {

    	try {
    		
    		CustomXmlDataStoragePart customXmlDataStoragePart 
    			= CustomXmlDataStoragePartSelector.getCustomXmlDataStoragePart(wordMLPackage);		
		
    		if (customXmlDataStoragePart==null) {
    			throw new Docx4JException("Couldn't find CustomXmlDataStoragePart");}
		
    		CustomXmlDataStorage xmlStorage = customXmlDataStoragePart.getData();
    		org.w3c.dom.Document xmlDoc = xmlStorage.getDocument();
    		return xmlDoc.getDocumentElement().getNamespaceURI().toString();
		
    	} catch( Exception e) {
    		
    		e.printStackTrace();
    		
    	}
    	
		return null;
    }
    
	public void renderWordDocNode(NodeRef nodeRef, String data) throws Docx4JException, IOException{
		
		try {

			openWordDoc(nodeRef);
			ByteArrayInputStream XmlInputStream = new ByteArrayInputStream(data.getBytes());
			addDataContent(XmlInputStream);
			applyDataToDocument();
			writeToNodeContent(nodeRef);
			
		} catch(Exception e) {
			
			logger.error("Unable to render word document nodeRef[" + nodeRef.toString() + "] data [" + data + "] " + e.getMessage());
			throw e;
			
		}
		
	}
	
	public void updateWordDoc(String data)  throws WordRendererException {

		if(this.wordMLPackage == null) {
			throw new WordRendererException("Document is not opened, must be opened before an update");
		}
		
		if(this.nodeRef == null) {
			throw new WordRendererException("A NodeRef has not been set, must be set so we can write back to it");
		}
		
		
		try {

			ByteArrayInputStream XmlInputStream = new ByteArrayInputStream(data.getBytes());
			addDataContent(XmlInputStream);
			applyDataToDocument();
			writeToNodeContent(this.nodeRef);
			
		} catch(Exception e) {
			
			StringBuilder sb = new StringBuilder();
			sb.append("Unable to render word document nodeRef [");
			sb.append(this.nodeRef.toString());
			sb.append("] data [");
			sb.append(data);
			sb.append("] ");
			
			WordRendererException ee = new WordRendererException(sb.toString(), e);
			throw ee;
			
		}

	}

	/**
	 * Takes the list of Alfresco node properties and applies them to the
	 * customXMLpart of the word document.  The MapperFactory provides mapping
	 * between the Alfresco properties and the customXMLpart.
	 * 
	 * @param mapperFactory a mapper factory which provides the mapping information
	 * @param properties a map of Alfresco QName and the associated values
	 * @throws WordRendererException 
	 * 
	 */
	public void updateCustomData(Map<QName, Serializable> properties) throws WordRendererException {
		
		if(properties == null) {
			
			logger.warn("trying to update Word node [" + this.nodeRef.toString() + "] properties but properties supplied to 'updateCustomData' are null");
			return;
			
		}
		
		// Find custom xml file and update the XML nodes
		try {
			
			/**
			 * get a handle on the XML Document object for the customXMLpart
			 * 
			 */
			CustomXmlDataStoragePart customXmlDataStoragePart 
				= CustomXmlDataStoragePartSelector.getCustomXmlDataStoragePart(wordMLPackage);		
				
			if (customXmlDataStoragePart==null) {
			    throw new Docx4JException("Couldn't find CustomXmlDataStoragePart");}
			
			org.w3c.dom.Document xmlDoc = customXmlDataStoragePart.getData().getDocument();
    		
			String CustomXMLNamespace = this.getCustomXMLNamespace();
			if(CustomXMLNamespace == null) {
				throw new WordMapperException("Document does not contain CustomXMLNamespace [" + this.nodeRef + "]");
			}
			
			IMapper mapper;
			try {
			    mapper = MapperFactory.getMapperForNamespaceURI(CustomXMLNamespace);
			} catch (Exception e) {
				e.printStackTrace();
				throw new WordMapperException("No mapper available for namespace [" + CustomXMLNamespace + "] ");
			}
			
			/**
			 * traverse the nodes and if we have a corresponding property
			 * update the text value of the node
			 * 
			 */
			
			/**
			NodeList nodeList = xmlDoc.getElementsByTagName("*");
			for(int i=0; i<nodeList.getLength(); i++) {
				
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ENTITY_NODE ) {
					
					javax.xml.namespace.QName xmlNodeName = new javax.xml.namespace.QName(CustomXMLNamespace, node.getLocalName());
					if(mapper.getMap().containsKey(xmlNodeName)) {
						
						
						List<QName> alfPropNames = mapper.getMap().get(xmlNodeName);
						StringBuilder sb = new StringBuilder();
						for(QName alfPropName : alfPropNames) {
							
							Serializable s = properties.get(alfPropName);
							if( s != null) {
								
								if(sb.length() > 1) {
									sb.append(" ");
								}
								sb.append(s.toString());
							}
						}
						
						node.setTextContent(sb.toString());
					}
				}
			
			}*/
			putPropertyInNode(xmlDoc.getDocumentElement(), properties, mapper);
			this.applyDataToDocument();
			
			logger.debug("Updated the customXMLdata content ");
		
		} catch(Exception e) {
			
			WordRendererException ee = new WordRendererException("Error mapping to CustomXMLComponent", e);
			ee.printStackTrace();
			throw ee;
		}
	}
	
	private void putPropertyInNode(Node node, Map<QName, Serializable> properties, IMapper mapper) {
		
		
			
			javax.xml.namespace.QName xmlNodeName = new javax.xml.namespace.QName(node.getNamespaceURI(), node.getLocalName());
			if(mapper.getMap().containsKey(xmlNodeName)) {
				
				List<QName> alfPropNames = mapper.getMap().get(xmlNodeName);
				StringBuilder sb = new StringBuilder();
				Boolean setContent = Boolean.FALSE;
				for(QName alfPropName : alfPropNames) {
					
					if(properties.containsKey(alfPropName)) {
						
						Serializable s = properties.get(alfPropName);
						if( s != null) {
							
							setContent = Boolean.TRUE;
							if(sb.length() > 1) {
								sb.append(" ");
							}
							sb.append(s.toString());
						}
					}
				}
				
				if(setContent) {
				    node.setTextContent(sb.toString());}
			}
		
		
		NodeList nodeList = node.getChildNodes();
	    for (int i = 0; i < nodeList.getLength(); i++) {
	        Node currentNode = nodeList.item(i);
	        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	            //calls this method for all the children which is Element
	            putPropertyInNode(currentNode, properties, mapper);
	        }
	    }
	}
	
    protected void openWordDoc(NodeRef nodeRef) throws Docx4JException, IOException {
		
		// Read data associated with a content NodeRef (plain text)
		ContentService contentService = registry.getContentService();
		ContentReader reader = contentService.getReader(nodeRef, ContentModel.TYPE_CONTENT);
		
		
		InputStream is = reader.getContentInputStream();
		wordMLPackage = WordprocessingMLPackage.load(is);
		is.close();
		
		logger.debug("Created the WordML package for node [" + nodeRef + "]");
		
	}
	
    /**
     * Replaces the customXMLpart with a new document read from the
     * supplied input stream
     * 
     * @param xis
     * @throws Docx4JException
     */
	protected void addDataContent(InputStream xis) throws Docx4JException {
		
		// Find custom xml item id and inject data_file.xml		
		CustomXmlDataStoragePart customXmlDataStoragePart 
		 = CustomXmlDataStoragePartSelector.getCustomXmlDataStoragePart(wordMLPackage);		

		
		if (customXmlDataStoragePart==null) {
		    throw new Docx4JException("Couldn't find CustomXmlDataStoragePart");}
		customXmlDataStoragePart.getData().setDocument(xis);
		
		logger.debug("Added the data content ");
	}
	
	/**
	 * Updates the main document content with data from the
	 * customXMLpart.
	 * 
	 * @throws Docx4JException
	 */
	protected void applyDataToDocument() throws Docx4JException {
		
		BindingHandler bh = new BindingHandler(wordMLPackage);
		bh.applyBindings(wordMLPackage.getMainDocumentPart());
		
		logger.debug("Applied data to word document ");
	}
	
	/**
	 * 
	 * @param nodeRef
	 * @throws Docx4JException
	 * @throws IOException
	 */
	public void writeToNodeContent(NodeRef nodeRef) throws Docx4JException, IOException {
		
		ContentService contentService = registry.getContentService();
		ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
		writer.setMimetype(DOCX_MIME_TYPE);

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		wordMLPackage.save(outStream);
		InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
		writer.putContent(inStream);
		outStream.close();
		inStream.close();
		
		logger.debug("Updated node with new content");
	}
}
