package com.mwt.activiti.handlers.roleContract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.contract.ContractService;
import com.mwt.contract.activiti.ApplyRightToWorkAspectFromProcess;
import com.mwt.contract.model.ServicePeriodModel;
import com.mwt.contract.propertyBuilder.ContractContentBuilder;
import com.mwt.production.ContractDocumentTypes;
import com.nvp.alfresco.docx.WordPropertiesManager;
import com.nvp.util.DocUtil;
import com.nvp.util.NodeRefUtil;

/**
 * 
 * Updates the contact document with the current role information
 * 
 * This class is called by the role-management-process once the role details have been confirmed
 * The properties are extracted from the workflow and updated on the contract document meta data
 * The meta data is then pushed into the document and the document content re-rendered
 * 
 * @author nick
 *
 */
public class MergePropertiesToContractDocument extends AbstractAlfrescoListener implements TaskListener{

	/**
	 * 
	 * the name of the workflow property which contains the document package node ref
	 * 
	 */
	private static String bpm_package = "bpm_package";
	
	/**
	 * 
	 * 
	 */
	@Override
	public void notify(DelegateTask task) {
		
		DelegateExecution exec = task.getExecution();
		
		/**
		 * 
		 * get the Employment contract in the workflow package
		 * 
		 */
		List<NodeRef> contractDocs = null;
		if(task.hasVariable(bpm_package)) {

			ActivitiScriptNode n = (ActivitiScriptNode) exec.getVariable(bpm_package);
			contractDocs = DocUtil.findDocumentByType(n.getNodeRef(), ContractDocumentTypes.EmploymentContract, this.getServiceRegistry());
        
			if(null == contractDocs || contractDocs.isEmpty()) {
				
				System.out.print("WARNING Attempting to update contract document in workflow " + exec.getProcessInstanceId() + " but didnt find an Employment contract in the workflow package");
				return;
				
			}
			
		} else {

			System.out.print("WARNING Attempting to update contract document in workflow " + exec.getProcessInstanceId() + " but the workflow had no package");
			return;

		}
		
		/**
		 * 
		 * merge the workflow properties into the employment contract document node
		 * 
		 */
		DocUtil.mergeContractDocumentAspect(exec, contractDocs.get(0), this.getServiceRegistry());
		DocUtil.mergeServicePeriodAspect(exec, contractDocs.get(0), this.getServiceRegistry());
		DocUtil.mergeIndividualSupplierAspect(exec, contractDocs.get(0), this.getServiceRegistry());
		DocUtil.mergeCrewEngagementAspect(exec, contractDocs.get(0), this.getServiceRegistry());
		DocUtil.mergeIndividualSupplierAspect(exec, contractDocs.get(0), this.getServiceRegistry());
		DocUtil.mergeBankAccountAspect(exec, contractDocs.get(0), this.getServiceRegistry());
		ApplyRightToWorkAspectFromProcess.Merge(exec, contractDocs.get(0), this.getServiceRegistry());
		
		/**
		 * 
		 * merge the contract document node properties into the contract documentNodement itself
		 * 
		 */
		Map<QName, Serializable> contractDocumentProperties = new HashMap<QName, Serializable>();
		NodeService nodeService = this.getServiceRegistry().getNodeService();
		contractDocumentProperties = nodeService.getProperties(contractDocs.get(0));

		/**
		 * get an array of servicePeriod nodes
		 */
		Set<QName> servicePeriodTypeName = new HashSet<QName>();
		servicePeriodTypeName.add(ServicePeriodModel.QN_SERVICEPERIOD_TYPE);
		List<ChildAssociationRef> childRefs = nodeService.getChildAssocs(contractDocs.get(0), servicePeriodTypeName);
		
		/**
		 * 
		 * build the XML to merge into the document
		 * 
		 */
		ContractContentBuilder contentBuilder = new ContractContentBuilder();
		try {
		contentBuilder.propertiesToCustomContent(contractDocumentProperties);
		if(!childRefs.isEmpty()) {
			Map<QName, Serializable> servicePeriodProperties = new HashMap<QName, Serializable>();
			for(ChildAssociationRef childAssoc : childRefs ) {
				servicePeriodProperties = nodeService.getProperties(childAssoc.getChildRef());
				
				String servicePeriodId = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICEPERIOD_ID);
				String servicePeriodName = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICEPERIOD_NAME); 
				String servicePeriodDesc = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICEPERIOD_DESCRIPTION); 
				String serviceTypeCode = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICE_TYPE_CODE);
				String servicePeriodType = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICE_PERIOD_TYPE);
				String serviceStart = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICE_START).toString(); 
				String serviceEnd = (String) servicePeriodProperties.get(ServicePeriodModel.QN_SERVICE_END).toString();
				
				        contentBuilder.addServicePeriod(servicePeriodId,
				        								servicePeriodName, 
				        								servicePeriodDesc, 
				        								serviceTypeCode, 
				        								servicePeriodType,
				        								serviceStart, 
				        								serviceEnd);
			}

		}
		
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		
		try {
			
		WordPropertiesManager wordPropertiesManager = new WordPropertiesManager();
		wordPropertiesManager.setServiceRegistry(this.getServiceRegistry());
	    wordPropertiesManager.setWordNodeRef(contractDocs.get(0));
	    wordPropertiesManager.mergeProperties(contentBuilder.getDocument());
	    wordPropertiesManager.writeToNodeContentAsPDF(contractDocs.get(0));

        /**
         * 
         * set the contract document status
         * 
         */
        ContractService contractService = new ContractService();
        contractService.setServiceRegistry(this.getServiceRegistry());
        contractService.setStatusSupplierReview( contractDocs.get(0).getId());

        System.out.println("Contract document completed...");
        
		} catch (Exception e) {
		
			System.out.println(e);
		
		}
	}
	
}
