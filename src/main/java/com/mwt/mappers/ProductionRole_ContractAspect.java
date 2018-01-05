package com.mwt.mappers;

import org.alfresco.service.namespace.QName;
import java.util.Map;
import java.io.Serializable;

import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.roles.ProductionRoleModel;

public class ProductionRole_ContractAspect {

	public static ContractDocumentModel mapToContractAspect(Map<QName, Serializable> productionRole) {
		
		ContractDocumentModel contractModel = new ContractDocumentModel();
		return contractModel;
		
	}
}
