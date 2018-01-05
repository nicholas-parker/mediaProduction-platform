package com.mwt.word.mapper;
 
import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import com.nvp.alfresco.docx.IMapper;
import com.nvp.alfresco.docx.MapperFactory;
import com.nvp.alfresco.docx.WordRenderer;

public class WordTransformer {
 
	
	/**
	 * Applies the supplied properties to the word document 
	 * in the node content.  The document is updated with
	 * the new values.
	 *  
	 * @param nodeRef
	 * @param properties
	 * @param registry
	 * @throws WordMapperException
	 * 
	 */
	public void updateWordDocument(NodeRef nodeRef, Map<QName, Serializable> properties, ServiceRegistry registry) throws WordMapperException {
		
		/**
		 * nodeRef must contain an MSWord content which has been 
		 * developed to contain custom XML data parts.
		 * 
		 * First step is to get the namespace of the custom XML data and then 
		 * obtain the correct mapper which will map the properties provided
		 * into the target XML namespace which is represented as an XMLDocument
		 */
		try {
			
			WordRenderer wordML = new WordRenderer(nodeRef, registry);
			wordML.updateCustomData(properties);
			wordML.writeToNodeContent(nodeRef);
			
		} catch (Exception e) {
			
			StringBuilder sb = new StringBuilder();
			sb.append("Exception whilst attempting to update a word node [");
			sb.append(nodeRef.toString());
			sb.append("] ");
			WordMapperException ee = new WordMapperException(sb.toString(),e);
			ee.printStackTrace();
			throw ee;
			
		}
	}
	
	public Map<QName, Serializable> getNodePropertyValues(NodeRef nodeRef) {
		return null;
	}

}