package com.mwt.activiti.handlers.roleContract;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.contract.model.INdividu;
import com.mwt.roles.ProductionRoleException;
import com.mwt.roles.ProductionRoleManager;
import com.mwt.roles.ProductionRoleModel;
import com.nvp.util.DateUtil;
import com.nvp.util.MapperUtil;
import com.nvp.util.NodeRefUtil;
import com.nvp.util.PaymentValueUtil;

public class UpdateRoleAfterContractSetup extends AbstractAlfrescoListener implements TaskListener {

	/**
	 * 
	 * the name of the workflow property which contains the node UUID of the crew role we are managing
	 * 
	 */
	private static String nvpList_roleNodeRef = "nvpList_roleNodeRef";
    private static String nvpList_budgetMin = "nvpList_budgetMin";
    private static String nvpList_budgetMax = "nvpList_budgetMax";
    private static String contract_ratePeriodSpecifier = "contract_ratePeriodSpecifier";

    /**
	 * 
	 * User has updated the contract details and sent out to the supplier.
	 * Some of the contract details will impact the role record so we need to align it.
	 * The items will be start & end dates + rate
	 * 
	 */
	@Override
	public void notify(DelegateTask task) {
		
		DelegateExecution exec = task.getExecution();

		
        String roleNodeUUID = null;
		if(task.hasVariable(nvpList_roleNodeRef)) {
			
			roleNodeUUID = NodeRefUtil.UUIDfromURL( task.getVariable(nvpList_roleNodeRef).toString() );
			
		} else {
			
			System.out.println("ERROR: unable to update status of role as this process doesnt contain a roleNodeRef property");
			
		}
		
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		MapperUtil util = new MapperUtil();
		util.setNamespaceService(this.getServiceRegistry().getNamespaceService());
		util.taskToMap(properties, ProductionRoleModel.QN_START_DATE, exec, INdividu.QN_SERVICE_START);
		util.taskToMap(properties, ProductionRoleModel.QN_END_DATE, exec, INdividu.QN_SERVICE_END);
		
		Date startDate;
		Date endDate;
		try {
		
			
			startDate = DateUtil.toDate(properties.get(ProductionRoleModel.QN_START_DATE));
			endDate = DateUtil.toDate( properties.get(ProductionRoleModel.QN_END_DATE));
			Float rate =  Float.valueOf( util.getExecVar(exec, ContractDocumentModel.QN_CONTRACT_VALUE).toString() );
			float totalContractsAmount = PaymentValueUtil.getTotalPaymentDayRate(startDate, endDate, rate, 5);
			
			properties.put(ProductionRoleModel.QN_TOTAL_CONTRACTS_AMOUNT, totalContractsAmount);
			
			ProductionRoleManager roleManager = new ProductionRoleManager();
			roleManager.setServiceRegistry(getServiceRegistry());
            roleManager.setTotalContractsValue(roleNodeUUID, totalContractsAmount);
			
            int budgetMin = 0;
            int budgetMax = 0;
            if(task.hasVariable(nvpList_budgetMin) && task.hasVariable(nvpList_budgetMax) && task.hasVariable(contract_ratePeriodSpecifier)) {

            	try {
            		
            	budgetMin = Integer.parseInt( task.getVariable(nvpList_budgetMin).toString() );
            	budgetMax = Integer.parseInt( task.getVariable(nvpList_budgetMax).toString() );
            	roleManager.setTotalBudgetMinMax(roleNodeUUID, 
            			                         startDate, 
            			                         endDate, 
            			                         budgetMin, 
            			                         budgetMax, 
            			                         task.getVariable(contract_ratePeriodSpecifier).toString());
            	} catch (Exception e) {

        			System.out.println("ERROR: Unable to update Role min/max budget after contract setup");
        			e.printStackTrace();

            	}
            } 
            
			
		} catch (ProductionRoleException e) {
			
			System.out.println("ERROR: Unable to update Role totalContractsAmount after contract setup");
			e.printStackTrace();
		
		} catch (ParseException pe) {
			
			System.out.println("ERROR: Unable to update Role totalContractsAmount after contract setup");
			pe.printStackTrace();
		
		}
		 
		
	}
}
