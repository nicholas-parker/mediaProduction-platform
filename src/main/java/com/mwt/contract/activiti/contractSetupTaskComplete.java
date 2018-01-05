package com.mwt.contract.activiti;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.solr.common.util.Hash;

import com.nvp.util.MapperUtil;

public class contractSetupTaskComplete implements TaskListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * copies the values of the contract setup task workflow 
	 * form into workflow variables
	 * 
	 */
	@Override
	public void notify(DelegateTask task) {
		
		
		DelegateExecution execution = task.getExecution();
		
		Map<String, Object> execVars = execution.getVariables();
		MapperUtil.printVars(execVars);
	
		//
		// Role
		//
		/*execution.setVariable("contract_location", task.getVariableLocal("contract_location"));
		execution.setVariable("contract_serviceName", task.getVariableLocal("contract_serviceName"));
		execution.setVariable("contract_serviceDescription", task.getVariableLocal("contract_serviceDescription"));
		execution.setVariable("contract_serviceTypeCode", task.getVariableLocal("contract_serviceTypeCode"));

		execution.setVariable("mwtwf_newSupplierFirstName", task.getVariableLocal("mwtwf_newSupplierFirstName"));
		execution.setVariable("mwtwf_newSupplierLastName", task.getVariableLocal("mwtwf_newSupplierLastName"));
		execution.setVariable("mwtwf_newSupplierEmail", task.getVariableLocal("mwtwf_newSupplierEmail"));

		execution.setVariable("contract_serviceStart", task.getVariableLocal("contract_serviceStart"));
		execution.setVariable("contract_serviceEnd", task.getVariableLocal("contract_serviceEnd"));
*/
		//
		// Contract
		//
		/*execution.setVariable("contract_PAYEstatus",task.getVariableLocal("contract_PAYEstatus"));
		execution.setVariable("contract_paymentPeriod", task.getVariableLocal("contract_paymentPeriod"));
		execution.setVariable("contract_noticePeriod", task.getVariableLocal("contract_noticePeriod"));
		execution.setVariable("contract_workingWeek", task.getVariableLocal("contract_workingWeek"));
		execution.setVariable("contract_holidayRate", task.getVariableLocal("contract_holidayRate"));
		execution.setVariable("contract_contractCode", task.getVariableLocal("contract_contractCode"));
		execution.setVariable("contract_contractDate", task.getVariableLocal("contract_contractDate"));
		execution.setVariable("contract_overtimePayable", task.getVariableLocal("contract_overtimePayable"));
		execution.setVariable("contract_overtimeRate", task.getVariableLocal("contract_overtimeRate"));
		execution.setVariable("contract_workingPeriod", task.getVariableLocal("contract_workingPeriod"));

*/
	}
	
	
}
