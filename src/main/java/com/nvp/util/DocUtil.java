package com.nvp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.task.Task;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import com.mwt.contract.model.BankAccountModel;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.contract.model.INdividu;
import com.mwt.contract.model.IndividualSupplierModel;
import com.nvp.alfresco.docx.WordRenderer;
import com.nvp.alfresco.docx.WordRendererException;

public class DocUtil {

	
	/**
	 * 
	 * Finds a document node which has a prod:docName attribute
	 * 
	 * @param nodeRef parent nodeRef to start searching
	 * @param docName the value of the prod:docName attribute
	 * @return a list of matching nodes
	 */
   public static List<NodeRef> findDocumentByType(NodeRef nodeRef, String type, ServiceRegistry serviceRegistry) {
	
		System.out.println("INFO: DocUtil attempting to find a document type [" + type + "] in child of node [" + nodeRef.toString() + "]");
		
		String luceneQuery = "@prod\\:docName = 'Employment contract' +PARENT:\"" + nodeRef.toString() + "\"";
				
		SearchService search = serviceRegistry.getSearchService();
		List<NodeRef> nodes = null;
		
		ResultSet results = null;
		try {
		
			results = search.query(nodeRef.getStoreRef(),
					     SearchService.LANGUAGE_LUCENE,
					     luceneQuery);
			
			if(results.length() > 0) {

				nodes = results.getNodeRefs();

			} else {
				
				System.out.println("WARN: DocUtil attempting to find a document type [" + type + "] in child of node [" + nodeRef.toString() + "], result list was empty");
				return null;
				
			}
			

		} catch ( Exception e) {
			
			System.out.println(e);
			return null;
			
		} finally {
			
			if( null != results) {
			  results.close();  }
		}
		
		if( !NodeRef.isNodeRef(nodes.get(0).toString())) {
			
			System.out.println("WARN: DocUtil attempting to find a document type [" + type + "] in child of node [" + nodeRef.toString() + "], noderef was not valid");
			return null;
		}
		
		System.out.println("INFO: DocUtil attempting to find a document type [" + type + "] in child of node [" + nodeRef.toString() + "], result was [" + nodes.get(0).toString());
		return nodes;
		
	}
   
   /**
    * 
    * merges the contract:contractDocument properties from a workflow onto the contract document node
    * 
    * @param exec
    */
   public static void mergeContractDocumentAspect(DelegateExecution exec, NodeRef contractDocumentNode, ServiceRegistry serviceRegistry) {
	  
	   MapperUtil mapper = new MapperUtil();
	   mapper.setNamespaceService(serviceRegistry.getNamespaceService());
	   NodeService nodeService = serviceRegistry.getNodeService();
	   
	   Map<QName, Serializable> contractDocumentAspectProperties = new HashMap<QName, Serializable>();
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_CONTRACT_DATE, exec);
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_CONTRACT_CODE, exec);
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_PAYE_STATUS, exec);
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_CONTRACT_VALUE, exec);
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_RATE_PERIOD_SPECIFIER, exec);
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_CONTRACT_VALUE_CURRENCY, exec);
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_CONTRACT_PAYMENT_PERIOD_SPECIFIER, exec);
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_WORKING_WEEK, exec);
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_OVERTIME_PAYABLE, exec);
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_OVERTIME_RATE, exec);
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_HOLIDAY_RATE, exec);
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_CONTACT_NOTICE_PERIOD, exec);
       mapper.taskToMap(contractDocumentAspectProperties, ContractDocumentModel.QN_LOCATION, exec);
	   
	   if( !nodeService.hasAspect(contractDocumentNode, ContractDocumentModel.QN_CONTRACT_ASPECT)) {
			 nodeService.addAspect(contractDocumentNode, ContractDocumentModel.QN_CONTRACT_ASPECT, contractDocumentAspectProperties);
	   } else {
		   NodeRefUtil.mergeNodeProperties(contractDocumentNode, contractDocumentAspectProperties, nodeService);
	   }
   }
   
   public static void mergeServicePeriodAspect(DelegateExecution exec, NodeRef contractDocumentNode, ServiceRegistry serviceRegistry) {
     
	   MapperUtil mapper = new MapperUtil();
	   mapper.setNamespaceService(serviceRegistry.getNamespaceService());
	   NodeService nodeService = serviceRegistry.getNodeService();
	   
	   Map<QName, Serializable> servicePeriodAspectProperties = new HashMap<QName, Serializable>();
	   mapper.taskToMap(servicePeriodAspectProperties, INdividu.QN_SERVICE_NAME, exec);
	   mapper.taskToMap(servicePeriodAspectProperties, INdividu.QN_SERVICE_DESCRIPTION, exec);
	   mapper.taskToMap(servicePeriodAspectProperties, INdividu.QN_SERVICE_START, exec);
	   mapper.taskToMap(servicePeriodAspectProperties, INdividu.QN_SERVICE_END, exec);

	   if( !nodeService.hasAspect(contractDocumentNode, INdividu.QN_SERVICE_PERIOD_ASPECT)) {
			 nodeService.addAspect(contractDocumentNode, ContractDocumentModel.QN_CONTRACT_ASPECT, servicePeriodAspectProperties);
	   } else {
		   NodeRefUtil.mergeNodeProperties(contractDocumentNode, servicePeriodAspectProperties, nodeService);
	   }

   }	   
   
   public static void mergeIndividualSupplierAspect(DelegateExecution exec, NodeRef contractDocumentNode, ServiceRegistry serviceRegistry) {

	   MapperUtil mapper = new MapperUtil();
	   mapper.setNamespaceService(serviceRegistry.getNamespaceService());
	   NodeService nodeService = serviceRegistry.getNodeService();
	   
	   Map<QName, Serializable> individualSupplierAspectProperties = new HashMap<QName, Serializable>();
	   mapper.taskToMap(individualSupplierAspectProperties, IndividualSupplierModel.QN_SUPPLIER_FIRST_NAME, exec);
	   mapper.taskToMap(individualSupplierAspectProperties, IndividualSupplierModel.QN_SUPPLIER_LAST_NAME, exec);
	   mapper.taskToMap(individualSupplierAspectProperties, IndividualSupplierModel.QN_SUPPLIER_ADDRESS1, exec);
	   mapper.taskToMap(individualSupplierAspectProperties, IndividualSupplierModel.QN_SUPPLIER_ADDRESS2, exec);
	   mapper.taskToMap(individualSupplierAspectProperties, IndividualSupplierModel.QN_SUPPLIER_ADDRESS3, exec);
	   mapper.taskToMap(individualSupplierAspectProperties, IndividualSupplierModel.QN_SUPPLIER_POST_CODE, exec);
	   mapper.taskToMap(individualSupplierAspectProperties, IndividualSupplierModel.QN_SUPPLIER_TELEPHONE, exec);

	   if( !nodeService.hasAspect(contractDocumentNode, IndividualSupplierModel.QN_INDIVIDUAL_SUPPLIER_ASPECT)) {
			 nodeService.addAspect(contractDocumentNode, IndividualSupplierModel.QN_INDIVIDUAL_SUPPLIER_ASPECT, individualSupplierAspectProperties);
	   } else {
		   NodeRefUtil.mergeNodeProperties(contractDocumentNode, individualSupplierAspectProperties, nodeService);
	   }

   }

   public static void mergeBankAccountAspect(DelegateExecution exec, NodeRef contractDocumentNode, ServiceRegistry serviceRegistry) {

	   MapperUtil mapper = new MapperUtil();
	   mapper.setNamespaceService(serviceRegistry.getNamespaceService());
	   NodeService nodeService = serviceRegistry.getNodeService();
	   
	   Map<QName, Serializable> bankAccountAspectProperties = new HashMap<QName, Serializable>();
	   mapper.taskToMap(bankAccountAspectProperties, BankAccountModel.QN_BANK_NAME, exec);
	   mapper.taskToMap(bankAccountAspectProperties, BankAccountModel.QN_BANK_ACCOUNT_NAME, exec);
	   mapper.taskToMap(bankAccountAspectProperties, BankAccountModel.QN_BANK_ACCOUNT_NUMBER, exec);
	   mapper.taskToMap(bankAccountAspectProperties, BankAccountModel.QN_BANK_BRANCH_SORT_CODE, exec);

	   if( !nodeService.hasAspect(contractDocumentNode, BankAccountModel.QN_BANKACCOUNT_ASPECT)) {
			 nodeService.addAspect(contractDocumentNode, BankAccountModel.QN_BANKACCOUNT_ASPECT, bankAccountAspectProperties);
	   } else {
		   NodeRefUtil.mergeNodeProperties(contractDocumentNode, bankAccountAspectProperties, nodeService);
	   }

   }

   public static NodeRef createDocumentFromOriginalWithProperties(String name, NodeRef original, NodeRef targetParent, ServiceRegistry registry ) throws Exception {
   
	   /**
	    * 
	    * copy the node content into new node
	    * 
	    */
	   NodeRef newNode = createDocumentFromOriginal(name, original, targetParent, registry );
	   
	   /**
	    * 
	    * now copy properties
	    * 
	    */
	   
	   return newNode;
   }
   
   public static NodeRef createDocumentFromOriginal(String name, NodeRef original, NodeRef targetParent, ServiceRegistry registry ) throws Exception {

		/**
		 * 
		 * A way to write to the log file
		 * 
		 */
		Log logger = LogFactory.getLog(DocUtil.class);

		
        /**
         * 
         * create the new node
         * 		
         */
		Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
		props.put(ContentModel.PROP_NAME,name);
		
		
		NodeRef newContent = null;
		try {
		
			NodeService nodeService = registry.getNodeService();
			NamespaceService namespaceService = registry.getNamespaceService();
			
			newContent = nodeService.createNode(targetParent, 
					                            ContentModel.ASSOC_CONTAINS, 
					                            QName.createQName( NamespaceService.CONTENT_MODEL_1_0_URI, name),
					                            ContentModel.TYPE_CONTENT,
					                            props).getChildRef();
			
			logger.info("Created a duplicate node, now copying the content");
			
		} catch (Exception e) {
			
			logger.error("Error whilst making a duplicate node of " + original.toString() + " and attaching to parent " + targetParent.toString() + ", " + e);
			throw new Exception("Error whilst making a duplicate node of " + original.toString() + " and attaching to parent " + targetParent.toString() + ", " + e.getMessage());
		}

        /**
         * 
         * copy the content from the source to the new node
         * 
         */
		ContentService contentService = registry.getContentService();
		ContentReader reader = contentService.getReader(original, ContentModel.TYPE_CONTENT);
		if( null == reader ) {

			logger.error("trying to copy a document, unable to obtain ContentReader for original node " + original.toString());
			throw new Exception("Unable to obtain ContentReader for node " + original.toString());
		
		} 
		
		InputStream is = reader.getContentInputStream();
		if( null == is ) {

			logger.error("Trying to copy a document, unable to get ContentInputStream for original node " + original.toString());
			throw new WordRendererException("Unable to get ContentInputStream for node " + original.toString());
			
		}

		ContentWriter writer = contentService.getWriter(newContent, ContentModel.TYPE_CONTENT, true);
		writer.setMimetype( reader.getMimetype() );
		writer.setEncoding( reader.getEncoding() );
		writer.putContent(is);
        is.close();
        
		return newContent;

   }
}