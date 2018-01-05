package com.mwt.contract.activiti;

import java.io.Serializable;
import java.util.HashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import com.mwt.contract.model.RightToWorkModel;
import com.nvp.util.MapperUtil;
import com.nvp.util.NodeRefUtil;

public class ApplyRightToWorkAspectFromProcess {

	public static void Merge(DelegateExecution exec, NodeRef node, ServiceRegistry serviceRegistry) {
		
		MapperUtil mapper = new MapperUtil();
		mapper.setNamespaceService(serviceRegistry.getNamespaceService());
		NodeService nodeService = serviceRegistry.getNodeService();

		HashMap<QName, Serializable> contract_rightToWork_properties = new HashMap<QName, Serializable>();
		
		// date of birth 
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_DATE_OF_BIRTH, exec);
		// town of birth
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_TOWN_OF_BIRTH, exec);
        // country of birth
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_COUNTRY_OF_BIRTH, exec);
		// NI number
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_NATIONAL_INSURANCE_NUM, exec);
		// identity document reference
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_IDENTITY_DOCUMENT_REFERENCE, exec);
		// identity document type
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_IDENTITY_DOCUMENT_TYPE, exec);
		// identity document issues
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_IDENTITY_DOCUMENT_ISSUER, exec);
		// visa number
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_VISA_NUMBER, exec);
		// visa expiry date
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_VISA_EXPIRY_DATE, exec);
		// right to work asserted
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_RIGHT_TO_WORK_ASSERTED, exec);
		// right to work approved
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_RIGHT_TO_WORK_APPROVED, exec);
		// right to work approved date
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_RIGHT_TO_APPROVED_DATE, exec);
		// right to work renew date
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_RIGHT_TO_RENEW_DATE, exec);
		// right to work expire date
		mapper.taskToMap(contract_rightToWork_properties, RightToWorkModel.QNAME_RIGHT_TO_EXPIRE_DATE, exec);

		if(nodeService.hasAspect(node, RightToWorkModel.QNAME_RIGHT_TO_WORK_ASPECT)) {
		
			NodeRefUtil.mergeNodeProperties(node, contract_rightToWork_properties, nodeService);
			
		} else {
			
			nodeService.addAspect(node, 
					              RightToWorkModel.QNAME_RIGHT_TO_WORK_ASPECT, 
					              contract_rightToWork_properties);
		}
			

	}
}
