package com.mwt.activiti.handlers;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import com.mwt.activiti.AbstractAlfrescoListener;

public class TaskContextProductionPrivate extends AbstractAlfrescoListener implements TaskListener{

	public static String CONTEXT_PRODUCTION_PRIVATE = "CONTEXTPRODUCTIONPRIVATE";
	
	/**
	 * 
	 * Sets the CONTEXT_PRODUCTION_PRIVATE flag for the current task.
	 * Only displayed in a production task list to the assigned
	 * If not assigned shown to the workflow initiator
	 * 
	 * 
	 */
	@Override
	public void notify(DelegateTask task) {
		
		TaskService taskService = task.getExecution().getEngineServices().getTaskService();
		taskService.setVariableLocal( task.getId(), CONTEXT_PRODUCTION_PRIVATE, CONTEXT_PRODUCTION_PRIVATE);
		
		task.setCategory(CONTEXT_PRODUCTION_PRIVATE);
		
	}


}

